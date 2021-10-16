package com.example.archive.service;

import com.example.archive.common.ResponseZipFile;
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
     * <p> Otherwise return {@link HttpStatus#INTERNAL_SERVER_ERROR} and null path
     *
     * @param file     the file to zip
     * @param fileName the fileName of zipped file to storage
     * @return {@link ResponseZipFile} with zipped file path and response status
     */
    ResponseZipFile archive(File file, String fileName, String controlSum);
}
