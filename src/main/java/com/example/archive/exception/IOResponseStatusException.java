package com.example.archive.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IOResponseStatusException extends ResponseStatusException {
    public IOResponseStatusException(String reason) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, reason);
    }
}
