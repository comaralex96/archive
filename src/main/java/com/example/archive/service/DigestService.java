package com.example.archive.service;

import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface DigestService {

    /**
     * Compute md5 sum
     *
     * @param file the file to compute from
     * @return the md5 sum of given {@code file}; or {@link Optional#empty()} if got {@link java.io.IOException}
     */
    Optional<String> md5AsHex(@NonNull MultipartFile file);
}
