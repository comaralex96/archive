package com.example.archive.converter;

import com.example.archive.controller.data.ZippedFile;
import com.example.archive.view.data.ResponseZipFile;

public class ZippedFileConverter {
    /**
     * Convert {@link ZippedFile} to {@link ResponseZipFile}
     *
     * @param zippedFile the zip file to convert
     * @return {@link ResponseZipFile} converted from {@link ZippedFile}
     */
    public static ResponseZipFile convertTo(ZippedFile zippedFile) {
        return ResponseZipFile.builder()
                .httpStatus(zippedFile.getHttpStatus())
                .path(zippedFile.getPath())
                .build();
    }
}
