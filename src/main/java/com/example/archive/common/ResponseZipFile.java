package com.example.archive.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.nio.file.Path;

@Value
@AllArgsConstructor
@Builder
public class ResponseZipFile {
    public static final ResponseZipFile EMPTY = new ResponseZipFile(HttpStatus.INTERNAL_SERVER_ERROR, null);

    @NonNull
    HttpStatus httpStatus;
    @Nullable
    @Builder.Default
    Path path = null;
}
