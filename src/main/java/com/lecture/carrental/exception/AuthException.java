package com.lecture.carrental.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)        // yetkisiz talepler olduğunda  hata mesajı verir.
public class AuthException extends RuntimeException{

    public AuthException(String message) {
        super(message);
    }
}
