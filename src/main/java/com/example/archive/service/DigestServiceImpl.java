package com.example.archive.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class DigestServiceImpl implements DigestService {

    public static final Logger logger = LoggerFactory.getLogger(DigestServiceImpl.class);

    @Override
    public String md5AsHex(@NonNull MultipartFile file) {
        StringBuilder md5 = new StringBuilder();
        try (InputStream is = file.getInputStream()) {
            DigestUtils.appendMd5DigestAsHex(is, md5);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return md5.toString();
    }
}
