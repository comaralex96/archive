package com.example.archive.storage;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public interface ZipFileStorage {

    /**
     * Store {@code file} as {@code fileName} in zip archive {@code zipArchiveName}.zip
     *
     * @param file           the file to store
     * @param fileName       the name of stored {@code file}
     * @param zipArchiveName the name of zip archive
     * @return {@link Path} of stored file
     */
    Optional<Path> store(File file, String fileName, String zipArchiveName);
}
