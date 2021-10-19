package com.example.archive.view.data;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.nio.file.Path;

@Value
@Builder
public class ResponseZipFile {
    @NonNull
    HttpStatus httpStatus;
    @NonNull
    Path path;
}
