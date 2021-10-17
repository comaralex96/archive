package com.example.archive.service;

import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

public interface DigestService {

    /**
     * Compute md5 sum
     *
     * @param file the file to compute from
     * @return md5 sum of given {@code file}; or {@code null} if got {@link java.io.IOException}
     */
    String md5AsHex(@NonNull MultipartFile file);
}
