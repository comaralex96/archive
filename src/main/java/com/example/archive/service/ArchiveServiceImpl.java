package com.example.archive.service;

import com.example.archive.storage.ZipFileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ArchiveServiceImpl implements ArchiveService {

    public static final Logger logger = LoggerFactory.getLogger(ArchiveServiceImpl.class);

    private final Map<String, Path> archiveCache = new HashMap<>();

    private final ZipFileStorage zipFileStorage;

    @Autowired
    public ArchiveServiceImpl(ZipFileStorage zipFileStorage) {
        this.zipFileStorage = zipFileStorage;
    }

    @Override
    public Optional<Path> archive(File file, String fileName, String controlSum) {
        Optional<Path> zipFilePath = zipFileStorage.store(file, fileName, controlSum);
        zipFilePath.ifPresent(path -> archiveCache.put(controlSum, path));
        return zipFilePath;
    }
}
