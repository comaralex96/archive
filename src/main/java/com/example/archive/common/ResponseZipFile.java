package com.example.archive.common;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import java.nio.file.Path;
import java.util.Optional;

public class ResponseZipFile {

    public static final ResponseZipFile EMPTY = new ResponseZipFile(null, HttpStatus.INTERNAL_SERVER_ERROR);
    private Path path;
    private HttpStatus httpStatus;

    public ResponseZipFile(Path path, @NonNull HttpStatus httpStatus) {
        this.path = path;
        this.httpStatus = httpStatus;
    }

    public Optional<Path> getPath() {
        return Optional.ofNullable(path);
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
