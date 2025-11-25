package com.backend.evently.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class HandleAuthenticationException extends RuntimeException {
    public HandleAuthenticationException(String message) {
        super(message);
    }
}
