package com.example.archive.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DigestService {

    /**
     * Calculate md5 sum
     *
     * @param file file to calculate from
     * @return md5 sum ot given file
     */
    String md5AsHex(MultipartFile file) throws IOException;
}
