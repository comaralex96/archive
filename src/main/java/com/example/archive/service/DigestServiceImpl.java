package com.example.archive.service;

import com.example.archive.exception.IOResponseStatusException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class DigestServiceImpl implements DigestService {

    @Override
    public String md5AsHex(@NonNull MultipartFile file) {
        StringBuilder md5 = new StringBuilder();
        try (InputStream is = file.getInputStream()) {
            DigestUtils.appendMd5DigestAsHex(is, md5);
        } catch (IOException e) {
            throw new IOResponseStatusException(e.getMessage(), e);
        }
        return md5.toString();
    }
}
