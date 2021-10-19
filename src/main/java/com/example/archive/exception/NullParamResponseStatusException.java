package com.example.archive.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NullParamResponseStatusException extends ResponseStatusException {
    public NullParamResponseStatusException(String param) {
        super(HttpStatus.NOT_FOUND, "Param '" + param + "' is null");
    }
}
