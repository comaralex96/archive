package com.example.archive.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class FileNotFoundResponseStatusException extends ResponseStatusException {
    public FileNotFoundResponseStatusException(String reason, Throwable cause) {
        super(HttpStatus.NOT_FOUND, reason, cause);
        log.error(cause.getMessage(), cause);
    }
}
