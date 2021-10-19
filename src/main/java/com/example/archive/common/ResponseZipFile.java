package com.example.archive.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.nio.file.Path;

@Value
@AllArgsConstructor
@Builder
public class ResponseZipFile {
    @NonNull
    HttpStatus httpStatus;
    @NonNull
    Path path;
}
