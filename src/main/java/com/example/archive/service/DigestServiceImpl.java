package com.example.archive.service;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class DigestServiceImpl implements DigestService {

    @Override
    public String md5AsHex(MultipartFile file) throws IOException {
        StringBuilder md5 = new StringBuilder();
        try (InputStream is = file.getInputStream()) {
            DigestUtils.appendMd5DigestAsHex(is, md5);
        }
        return md5.toString();
    }
}
