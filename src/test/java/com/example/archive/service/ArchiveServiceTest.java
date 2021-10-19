package com.example.archive.service;

import com.example.archive.common.Constants;
import com.example.archive.controller.data.ZippedFile;
import com.example.archive.storage.ZipFileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ArchiveServiceTest {

    private final File file = new File("src/test/resources/1.txt");
    private final File sameFile = new File("src/test/resources/2.txt");
    private final File otherFile = new File("src/test/resources/1.md");
    private final String etag = "03a93ec0899bccfa901a58e07099348a";
    private final String otherEtag = "3497c1f843f8f66fa8b2011a601693c0";
    private final Path zipPath = Path.of(etag.concat(Constants.ZIP_EXTENSION));
    private final ZippedFile ok = new ZippedFile(HttpStatus.OK, zipPath);
    private final ZippedFile notModified = new ZippedFile(HttpStatus.NOT_MODIFIED, zipPath);
    private final Path zipOtherPath = Path.of(otherEtag.concat(Constants.ZIP_EXTENSION));
    private final ZippedFile otherOk = new ZippedFile(HttpStatus.OK, zipOtherPath);
    private final ZippedFile otherNotModified = new ZippedFile(HttpStatus.NOT_MODIFIED, zipOtherPath);
    @Mock
    private ZipFileStorage storage;
    @InjectMocks
    private ArchiveService archiveService = new ArchiveServiceImpl();

    @BeforeEach
    @DisplayName("Mock store method to get Path of inputFile")
    public void mockZipFileStorageMethods() {
        when(storage.store(file, file.getName(), etag)).thenReturn(zipPath);
        when(storage.store(sameFile, sameFile.getName(), etag)).thenReturn(zipPath);
        when(storage.store(otherFile, otherFile.getName(), otherEtag)).thenReturn(zipOtherPath);
        when(storage.exists(etag)).thenReturn(true);
        when(storage.exists(otherEtag)).thenReturn(true);
    }

    private ZippedFile archiveInputFile() {
        return archiveService.archive(file, file.getName(), etag);
    }

    private ZippedFile archiveSameFile() {
        return archiveService.archive(sameFile, sameFile.getName(), etag);
    }

    private ZippedFile archiveOtherFile() {
        return archiveService.archive(otherFile, otherFile.getName(), otherEtag);
    }

    @Test
    @DisplayName("Archive file and get status 200 Ok")
    public void testArchiveFile() {
        ZippedFile response = archiveInputFile();
        assertEquals(ok, response);
    }

    @Test
    @DisplayName("Archive file N times to get status 304 Not Modified")
    public void testArchiveFileNTimes() {
        testArchiveFile();
        ZippedFile response = archiveInputFile();
        assertEquals(notModified, response);
    }

    @Test
    @DisplayName("Archive different files")
    public void testArchiveDifferentFiles() {
        ZippedFile response;
        response = archiveInputFile();
        assertEquals(ok, response);
        response = archiveOtherFile();
        assertEquals(otherOk, response);
        response = archiveInputFile();
        assertEquals(notModified, response);
        response = archiveSameFile();
        assertEquals(notModified, response);
        response = archiveOtherFile();
        assertEquals(otherNotModified, response);
    }
}
