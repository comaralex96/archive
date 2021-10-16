package com.example.archive.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DigestService {

    /**
     * Compute md5 sum
     *
     * @param file the file to compute from
     * @return md5 sum of given {@code file}
     */
    String md5AsHex(MultipartFile file) throws IOException;
}
