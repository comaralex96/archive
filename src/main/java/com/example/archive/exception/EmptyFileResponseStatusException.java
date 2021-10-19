package com.example.archive.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmptyFileResponseStatusException extends ResponseStatusException {
    public EmptyFileResponseStatusException(String fileName) {
        super(HttpStatus.NOT_FOUND, "File '" + fileName + "' is empty", null);
    }
}
