package com.example.archive.service;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public interface ArchiveService {

    /**
     * Archive {@code file} to temporary directory
     *
     * @param file     the file to zip
     * @param fileName the fileName of zipped file to storage
     * @return zipped file
     */
    Optional<Path> archive(File file, String fileName, String controlSum);
}
