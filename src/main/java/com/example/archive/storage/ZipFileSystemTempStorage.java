package com.example.archive.storage;

import com.example.archive.common.Constants;
import com.example.archive.exception.FileNotFoundResponseStatusException;
import com.example.archive.exception.IOResponseStatusException;
import lombok.NonNull;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Repository
public class ZipFileSystemTempStorage implements ZipFileStorage {

    private final Path workspace;

    public ZipFileSystemTempStorage() throws IOException {
        workspace = Files.createTempDirectory(Constants.DIRECTORY_TEMP_PREFIX);
    }

    @Override
    public Path store(@NonNull File file, String fileName, String zipFileName) {
        Path path = workspace.resolve(zipFileName.concat(Constants.ZIP_EXTENSION));
        File zipFile = path.toFile();
        try (InputStream inputStream = new FileInputStream(file);
             OutputStream outputStream = new FileOutputStream(zipFile);
             var zipOutputStream = new ZipOutputStream(outputStream)) {
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            IOUtils.copy(inputStream, zipOutputStream);
            zipOutputStream.closeEntry();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundResponseStatusException(e.getMessage(), e);
        } catch (IOException e) {
            throw new IOResponseStatusException(e.getMessage(), e);
        }
        return path;
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
            throw new IOResponseStatusException(e.getMessage(), e);
        }
    }
}