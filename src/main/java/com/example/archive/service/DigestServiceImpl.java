package com.example.archive.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
@Service
public class DigestServiceImpl implements DigestService {

    @Override
    public Optional<String> md5AsHex(@NonNull MultipartFile file) {
        StringBuilder md5 = new StringBuilder();
        try (InputStream is = file.getInputStream()) {
            DigestUtils.appendMd5DigestAsHex(is, md5);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
        return Optional.of(md5.toString());
    }
}
