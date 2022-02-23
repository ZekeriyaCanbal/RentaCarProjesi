package com.lecture.carrental.repository;

import com.lecture.carrental.domain.User;
import com.lecture.carrental.exception.BadRequestException;
import com.lecture.carrental.exception.ResourceNotFoundException;
import com.lecture.carrental.projection.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional          // Tüm metodların tümünü eş zamanlı olarak yapmak için @Transactional anotasyonu kullandık.
public interface UserRepository extends JpaRepository<User, Long> {

//    @Query("SELECT u FROM User u WHERE u.email =?1") // Normalde yazmaya gerek yok. Spring Boot kendisi oluşturuyor. aşağıda yapıyor.
    Optional<User> findByEmail(String email);           // user veritabanında email sorgulaması yapıyor.(var mı yok mu) varsa bilgileri döndürüyor.

    Boolean existsByEmail(String email) throws ResourceNotFoundException;   //findByEmail kullanılabilir.

    List<ProjectUser> findAllBy();

    @Modifying
    @Query("UPDATE User u SET u.firstName=?2, u.lastName=?3, u.phoneNumber=?4, u.email=?5, u.address=?6, u.zipCode=?7 WHERE u.id=?1")
    void update(Long id, String firstName, String lastName, String phoneNumber, String email, String address, String zipCode) throws BadRequestException;
}
