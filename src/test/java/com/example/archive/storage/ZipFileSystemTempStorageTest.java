package com.example.archive.storage;

import com.example.archive.common.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ZipFileSystemTempStorageTest {

    private final ZipFileStorage storage = new ZipFileSystemTempStorage();

    private final Path workspace;

    private final File inputFile = new File("src/test/resources/1.txt");

    public ZipFileSystemTempStorageTest() throws IOException {
        workspace = (Path) ReflectionTestUtils.getField(storage, "workspace");
    }

    @Test
    @DisplayName("Storage create clean directory")
    public void testStoreFilesInCleanDirectory() {
        assertNotNull(workspace);
        assertTrue(Files.isDirectory(workspace));
        try {
            assertTrue(Files.list(workspace).findAny().isEmpty());
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    @DisplayName("Check zip file exists in temp directory")
    public void testExistsFileInStorage() {
        Path tempPath;
        try {
            tempPath = Files.createTempFile(workspace, "", Constants.zipExtension);
        } catch (IOException e) {
            fail(e);
            return;
        }
        String originalFileName = tempPath.getFileName().toString();
        String fileName = originalFileName.substring(0, originalFileName.length() - Constants.zipExtension.length());
        assertTrue(storage.exists(fileName));
        assertFalse(storage.exists("0" + fileName));
        assertFalse(storage.exists(fileName + "0"));
        try {
            Files.delete(tempPath);
        } catch (IOException e) {
            fail(e);
        }
        assertFalse(storage.exists(fileName));
    }

    @Test
    @DisplayName("Get file from storage")
    public void testGetFileFromStorage() {
        Path tempPath;
        try {
            tempPath = Files.createTempFile(workspace, "", Constants.zipExtension);
        } catch (IOException e) {
            fail(e);
            return;
        }
        String originalFileName = tempPath.getFileName().toString();
        String fileName = originalFileName.substring(0, originalFileName.length() - Constants.zipExtension.length());
        File storedFile = storage.getFileByName(fileName);
        File inputFile = tempPath.toFile();
        assertEquals(inputFile, storedFile);
        File notFoundPrefixFile = storage.getFileByName("0" + fileName);
        assertNull(notFoundPrefixFile);
        File notFoundSuffixFile = storage.getFileByName(fileName + "0");
        assertNull(notFoundSuffixFile);
        try {
            Files.delete(tempPath);
        } catch (IOException e) {
            fail(e);
        }
        storedFile = storage.getFileByName(fileName);
        assertNull(storedFile);
    }

    @ParameterizedTest
    @DisplayName("Store file in directory as zipFile 'etag.zip' with inner file 'filename'")
    @ValueSource(strings = {"03a93ec0899bccfa901a58e07099348a"})
    public void testStoreFileAsZipFile(String etag) {
        storage.store(inputFile, inputFile.getName(), etag);
        assertTrue(storage.exists(etag));
        File storedFile = storage.getFileByName(etag);
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(storedFile);
        } catch (IOException e) {
            fail(e);
            return;
        }
        Path filePath = workspace.resolve(etag.concat(Constants.zipExtension)).normalize();
        assertEquals(filePath.toString(), zipFile.getName());
        ZipEntry zipEntry = zipFile.getEntry(inputFile.getName());
        assertNotNull(zipEntry);
        assertEquals(inputFile.getName(), zipEntry.getName());
    }

    @Test
    @DisplayName("Clean directory on destroy")
    public void testCleanDirectoryOnDestroy() {
        assertTrue(Files.exists(workspace));
        ReflectionTestUtils.invokeMethod(storage, "onDestroy");
        assertFalse(Files.exists(workspace));
    }

//    @AfterEach
    @Disabled
    public void cleanDirectory() {
        try {
            Files.list(workspace).forEach(path -> {
                try {
                    FileSystemUtils.deleteRecursively(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            fail(e);
        }
    }
}
