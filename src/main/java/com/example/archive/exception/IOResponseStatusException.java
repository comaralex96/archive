package com.example.archive.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class IOResponseStatusException extends ResponseStatusException {
    public IOResponseStatusException(String reason, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, reason, cause);
        log.error(cause.getMessage(), cause);
    }
}
