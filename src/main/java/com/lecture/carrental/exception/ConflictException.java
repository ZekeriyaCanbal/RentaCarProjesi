package com.lecture.carrental.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)            //register olurken emailler aynı ise hata mesajı döner
public class ConflictException extends RuntimeException{

    public ConflictException(String message) {
        super(message);
    }

}
