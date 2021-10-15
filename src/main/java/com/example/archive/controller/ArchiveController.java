package com.example.archive.controller;

import com.example.archive.model.ZippedFile;
import com.example.archive.service.DigestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class ArchiveController {

    private final DigestService digestService;

    @Autowired
    public ArchiveController(DigestService digestService) {
        this.digestService = digestService;
    }

    @PostMapping("/zipFile")
    public ResponseEntity<ZippedFile> archive(@RequestPart("file") MultipartFile file) {
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        String md5;
        try {
            md5 = digestService.md5AsHex(file);
        } catch (IOException e) {
            return ResponseEntity.noContent().build();
        }
        ZippedFile result = new ZippedFile(file);
        result.setMd5(md5);
        return ResponseEntity.ok()
                .header(HttpHeaders.ACCEPT)
                .eTag(md5)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }
}
