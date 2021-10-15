package com.example.archive.controller;

import com.example.archive.model.ZippedFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ArchiveController {

    @PostMapping("/zipFile")
    public ResponseEntity<ZippedFile> archive(@RequestPart("file") MultipartFile file) {
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.ACCEPT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ZippedFile(file));
    }
}
