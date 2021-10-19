package com.example.archive.controller;

import com.example.archive.common.Constants;
import com.example.archive.common.ResponseZipFile;
import com.example.archive.exception.EmptyFileResponseStatusException;
import com.example.archive.exception.FileNotFoundResponseStatusException;
import com.example.archive.exception.IOResponseStatusException;
import com.example.archive.exception.NullParamResponseStatusException;
import com.example.archive.service.ArchiveService;
import com.example.archive.service.DigestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RestController
public class ArchiveController {

    private final DigestService digestService;

    private final ArchiveService archiveService;

    @Autowired
    public ArchiveController(DigestService digestService, ArchiveService archiveService) {
        this.digestService = digestService;
        this.archiveService = archiveService;
    }

    @PostMapping(value = "/zipFile", produces = Constants.APPLICATION_ZIP_VALUE)
    public ResponseEntity<Resource> archive(@RequestPart("file") MultipartFile file) {
        validateFile(file);
        String md5 = digestService.md5AsHex(file);
        ResponseZipFile responseZipFile = archiveAndGetFile(file, md5);
        final File zipFile = responseZipFile.getPath().toFile();
        InputStreamResource result;
        try {
            result = new InputStreamResource(new FileInputStream(zipFile));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundResponseStatusException(e.getMessage(), e);
        }
        final String attachment = convertAttachment(file.getOriginalFilename() + Constants.ZIP_EXTENSION);
        return ResponseEntity.status(responseZipFile.getHttpStatus())
                .header(HttpHeaders.CONTENT_DISPOSITION, attachment)
                .eTag(md5)
                .contentLength(zipFile.length())
                .body(result);
    }

    private ResponseZipFile archiveAndGetFile(MultipartFile file, String fileName) {
        ResponseZipFile responseZipFile;
        try {
            Path tempDirectory = Files.createTempDirectory(Constants.DIRECTORY_TEMP_PREFIX);
            File tempFile =
                    File.createTempFile(Constants.FILE_TEMP_PREFIX, Constants.FILE_TEMP_SUFFIX, tempDirectory.toFile());
            file.transferTo(tempFile);
            responseZipFile = archiveService.archive(tempFile, file.getOriginalFilename(), fileName);
            Files.delete(tempFile.toPath());
            Files.delete(tempDirectory);
        } catch (IOException e) {
            throw new IOResponseStatusException(e.getMessage(), e);
        }
        return responseZipFile;
    }

    private void validateFile(MultipartFile file) {
        if (file == null) {
            throw new NullParamResponseStatusException("file");
        }
        if (file.getOriginalFilename() == null) {
            throw new NullParamResponseStatusException(file.getName());
        }
        if (file.isEmpty()) {
            throw new EmptyFileResponseStatusException(file.getOriginalFilename());
        }
    }

    private String convertAttachment(String fileName) {
        ByteBuffer buffer = StandardCharsets.UTF_8.encode("attachment; filename=".concat(fileName));
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }
}
