package com.example.archive.storage;

import com.example.archive.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Repository
public class ZipFileSystemTempStorage implements ZipFileStorage {

    private final Path workspace;

    public ZipFileSystemTempStorage() throws IOException {
        workspace = Files.createTempDirectory(Constants.DIRECTORY_TEMP_PREFIX);
    }

    @Override
    public Optional<Path> store(File file, String fileName, String zipFileName) {
        if (file == null) {
            return Optional.empty();
        }
        Path path = workspace.resolve(zipFileName.concat(Constants.ZIP_EXTENSION));
        File zipFile = path.toFile();
        try (InputStream inputStream = new FileInputStream(file);
             OutputStream outputStream = new FileOutputStream(zipFile);
             var zipOutputStream = new ZipOutputStream(outputStream)) {
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            IOUtils.copy(inputStream, zipOutputStream);
            zipOutputStream.closeEntry();
        } catch (FileNotFoundException e) {
            log.error("Input file not found. " + e.getMessage(), e);
            return Optional.empty();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
        return Optional.of(path);
    }

    @Override
    public boolean exists(String zipArchiveName) {
        return Files.exists(workspace.resolve(zipArchiveName.concat(Constants.ZIP_EXTENSION)).normalize());
    }

    @Override
    public File getFileByName(String zipArchiveName) {
        if (!exists(zipArchiveName)) {
            return null;
        }
        return workspace.resolve(zipArchiveName.concat(Constants.ZIP_EXTENSION)).normalize().toFile();
    }

    @PreDestroy
    public void onDestroy() {
        try {
            FileSystemUtils.deleteRecursively(workspace);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}