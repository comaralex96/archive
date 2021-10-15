package com.example.archive.model;

import org.springframework.web.multipart.MultipartFile;

public class ZippedFile {

    private String name;
    private Long size;
    private String md5;

    public ZippedFile(MultipartFile file) {
        this.name = file.getOriginalFilename();
        this.size = file.getSize();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
