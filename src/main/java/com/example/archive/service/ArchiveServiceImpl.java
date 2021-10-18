package com.example.archive.service;

import com.example.archive.common.ResponseZipFile;
import com.example.archive.storage.ZipFileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ArchiveServiceImpl implements ArchiveService {

    private final Map<String, Path> archiveCache = new HashMap<>();

    private ZipFileStorage zipFileStorage;

    @Autowired
    public void setZipFileStorage(ZipFileStorage zipFileStorage) {
        this.zipFileStorage = zipFileStorage;
    }

    @Override
    public ResponseZipFile archive(File file, String fileName, String controlSum) {
        if (archiveCache.containsKey(controlSum) && zipFileStorage.exists(controlSum)) {
            return ResponseZipFile.builder()
                    .httpStatus(HttpStatus.NOT_MODIFIED)
                    .path(archiveCache.get(controlSum))
                    .build();
        }
        Optional<Path> zipFilePath = zipFileStorage.store(file, fileName, controlSum);
        if (zipFilePath.isEmpty()) {
            return ResponseZipFile.EMPTY;
        }
        archiveCache.put(controlSum, zipFilePath.get());
        return ResponseZipFile.builder()
                .httpStatus(HttpStatus.OK)
                .path(zipFilePath.get())
                .build();
    }
}
