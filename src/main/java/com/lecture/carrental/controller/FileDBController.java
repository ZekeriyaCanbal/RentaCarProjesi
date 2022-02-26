package com.lecture.carrental.controller;

import com.lecture.carrental.domain.FileDB;
import com.lecture.carrental.domain.dto.FileDTO;
import com.lecture.carrental.service.FileDBService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@AllArgsConstructor
@RestController
@RequestMapping(path = "/files")
@CrossOrigin("http://localhost:8081")
public class FileDBController {


    private final FileDBService fileDBService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file){

        try {

            FileDB fileDB = fileDBService.store(file);
            Map<String, String> map = new HashMap<>();
            map.put("imageId", fileDB.getId());

            return ResponseEntity.status(HttpStatus.OK).body(map);

        }catch (IOException e){

            Map<String, String> map = new HashMap<>();
            map.put("message", "Could not upload the file: "+file.getOriginalFilename()+"!");

            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(map);

        }
    }

    @GetMapping("")             //yüklelen tüm resim URL ve bilgilerini alıyor.
    public ResponseEntity<List<FileDTO>> getAllFiles(){
        List<FileDTO> files = fileDBService.getAllFile().map(dbFile ->{
            String fileDownloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/").path(dbFile.getId())
                    .toUriString();
            return new FileDTO(dbFile.getName(), fileDownloadUrl, dbFile.getType(), dbFile.getData().length);

        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);

    }

    @GetMapping("/{id}")        // id si verilen resim download ediliyor.
    public ResponseEntity<byte[]> getFile(@PathVariable String id){
        FileDB fileDB = fileDBService.getFileById(id);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=" + fileDB.getName()+ "")
        .body(fileDB.getData());
    }

    @GetMapping("/display/{id}")
    public ResponseEntity<byte[]> displayImage(@PathVariable String id) {
        FileDB fileDB = fileDBService.getFileById(id);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(fileDB.getData(), headers, HttpStatus.CREATED);
    }

}
