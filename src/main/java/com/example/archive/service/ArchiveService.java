package com.example.archive.service;

import java.io.File;

public interface ArchiveService {

    /**
     * Archive {@code file} to temporary directory
     *
     * @param file     the file to zip
     * @param fileName the fileName of zipped file to storage
     * @return zipped file
     */
    File archive(File file, String fileName, String controlSum);
}
