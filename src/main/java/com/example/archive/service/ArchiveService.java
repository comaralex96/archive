package com.example.archive.service;

import com.example.archive.controller.data.ZippedFile;
import org.springframework.http.HttpStatus;

import java.io.File;

public interface ArchiveService {

    /**
     * Archive {@code file} in storage
     *
     * <p> If successfully archive new file return {@link HttpStatus#OK} and path to zipped file
     *
     * <p> If file was found in cache return {@link HttpStatus#NOT_MODIFIED} and path to zipped file
     *
     * @param file     the file to zip
     * @param fileName the fileName of zipped file to storage
     * @return {@link ZippedFile} with zipped file path and response status
     */
    ZippedFile archive(File file, String fileName, String controlSum);
}
