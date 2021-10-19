package com.example.archive.controller.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.nio.file.Path;

@Value
@AllArgsConstructor
@Builder
public class ZippedFile {
    @NonNull
    HttpStatus httpStatus;
    @NonNull
    Path path;
}
