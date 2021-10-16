package com.example.archive.storage;

import com.example.archive.common.Constants;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

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

@Repository
public class ZipFileSystemTempStorage implements ZipFileStorage {

    private static final Logger logger = LoggerFactory.getLogger(ZipFileSystemTempStorage.class);

    private final Path workspace;

    public ZipFileSystemTempStorage() throws IOException {
        workspace = Files.createTempDirectory(Constants.directoryTempPrefix);
    }

    public Optional<Path> store(File file, String fileName, String zipFileName) {
        Path path = workspace.resolve(zipFileName.concat(Constants.zipExtension));
        File zipFile = path.toFile();
        try (InputStream inputStream = new FileInputStream(file);
             OutputStream outputStream = new FileOutputStream(zipFile);
             var zipOutputStream = new ZipOutputStream(outputStream)) {
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            IOUtils.copy(inputStream, zipOutputStream);
            zipOutputStream.closeEntry();
        } catch (FileNotFoundException e) {
            logger.error("Input file not found. " + e.getMessage(), e);
            return Optional.empty();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return Optional.empty();
        }
        return Optional.of(path);
    }

    @Override
    public boolean exists(String zipArchiveName) {
        return Files.exists(workspace.resolve(zipArchiveName.concat(Constants.zipExtension)));
    }

    @PreDestroy
    public void onDestroy() {
        try {
            Files.delete(workspace);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}