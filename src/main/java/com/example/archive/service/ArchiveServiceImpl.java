package com.example.archive.service;

import com.example.archive.controller.data.ZippedFile;
import com.example.archive.storage.ZipFileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class ArchiveServiceImpl implements ArchiveService {

    private final Map<String, Path> archiveCache = new HashMap<>();

    private ZipFileStorage zipFileStorage;

    @Autowired
    public void setZipFileStorage(ZipFileStorage zipFileStorage) {
        this.zipFileStorage = zipFileStorage;
    }

    @Override
    public ZippedFile archive(File file, String fileName, String controlSum) {
        if (archiveCache.containsKey(controlSum) && zipFileStorage.exists(controlSum)) {
            return ZippedFile.builder()
                    .httpStatus(HttpStatus.NOT_MODIFIED)
                    .path(archiveCache.get(controlSum))
                    .build();
        }
        Path zipFilePath = zipFileStorage.store(file, fileName, controlSum);
        archiveCache.put(controlSum, zipFilePath);
        return ZippedFile.builder()
                .httpStatus(HttpStatus.OK)
                .path(zipFilePath)
                .build();
    }
}
