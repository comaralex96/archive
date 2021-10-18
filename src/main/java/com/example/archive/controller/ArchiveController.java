package com.example.archive.controller;

import com.example.archive.common.Constants;
import com.example.archive.common.ResponseZipFile;
import com.example.archive.service.ArchiveService;
import com.example.archive.service.DigestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@RestController
public class ArchiveController {

    private final Logger logger = LoggerFactory.getLogger(ArchiveController.class);

    private final DigestService digestService;

    private final ArchiveService archiveService;

    @Autowired
    public ArchiveController(DigestService digestService, ArchiveService archiveService) {
        this.digestService = digestService;
        this.archiveService = archiveService;
    }

    @PostMapping(value = "/zipFile", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> archive(@RequestPart("file") MultipartFile file) {
        if (file.getOriginalFilename() == null || file.getSize() == 0) {
            return ResponseEntity.notFound().build();
        }
        Optional<String> md5 = digestService.md5AsHex(file);
        if (md5.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        ResponseZipFile responseZipFile = ResponseZipFile.EMPTY;
        try {
            Path tempDirectory = Files.createTempDirectory(Constants.DIRECTORY_TEMP_PREFIX);
            File tempFile =
                    File.createTempFile(Constants.FILE_TEMP_PREFIX, Constants.FILE_TEMP_SUFFIX, tempDirectory.toFile());
            file.transferTo(tempFile);
            responseZipFile = archiveService.archive(tempFile, file.getOriginalFilename(), md5.get());
            Files.delete(tempFile.toPath());
            Files.delete(tempDirectory);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        if (responseZipFile.getPath().isEmpty()) {
            return ResponseEntity.status(responseZipFile.getHttpStatus()).build();
        }
        final File zipFile = responseZipFile.getPath().get().toFile();
        InputStreamResource result;
        try {
            result = new InputStreamResource(new FileInputStream(zipFile));
        } catch (FileNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.status(responseZipFile.getHttpStatus())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getOriginalFilename() + Constants.ZIP_EXTENSION)
//                .cacheControl(CacheControl.maxAge(1, TimeUnit.SECONDS))
//                .cacheControl(CacheControl.noStore())
                .eTag(md5.get())
                .contentLength(zipFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(result);
    }
}
