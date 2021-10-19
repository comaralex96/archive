package com.example.archive.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DigestServiceTest {

    private static final String inputMd5Sum = "03a93ec0899bccfa901a58e07099348a";
    private static final String otherMd5Sum = "39d517e59c3a63f9bdfaa6a34699d0df";
    private final DigestService digestService = new DigestServiceImpl();
    private final MockMultipartFile inputFile = new MockMultipartFile(
            "file",
            "1.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Archive me!".getBytes()
    );
    private final MockMultipartFile sameFile = new MockMultipartFile(
            "sameFile",
            "2.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Archive me!".getBytes()
    );
    private final MockMultipartFile otherFile = new MockMultipartFile(
            "otherFile",
            "1.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, Archive!".getBytes()
    );

    @Test
    @DisplayName("Compute md5sum of file")
    public void testComputeMD5Sum() {
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> digestService.md5AsHex(null));
        assertNotEquals(inputMd5Sum, otherMd5Sum);
        assertEquals(inputMd5Sum, digestService.md5AsHex(inputFile));
        assertEquals(inputMd5Sum, digestService.md5AsHex(sameFile));
        assertEquals(otherMd5Sum, digestService.md5AsHex(otherFile));
    }
}
