package com.example.archive.storage;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public interface ZipFileStorage {

    /**
     * Store {@code file} as {@code fileName} in zip archive {@code zipArchiveName}.extension
     *
     * @param file           the file to store
     * @param fileName       the name of stored {@code file}
     * @param zipArchiveName the name of zip archive without extension
     * @return {@link Path} of stored file
     */
    Optional<Path> store(File file, String fileName, String zipArchiveName);

    /**
     * Tests whether a file exists.
     *
     * @param zipArchiveName the name of zip archive without extension
     * @return {@code true} if the file exists; {@code false} if the file does not exist
     */
    boolean exists(String zipArchiveName);

    /**
     * Get {@see File} from storage
     *
     * @param zipArchiveName the name of zip archive
     * @return the {@see File} from storage
     */
    File getFileByName(String zipArchiveName);
}
