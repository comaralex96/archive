package com.example.archive.service;

import com.example.archive.common.Constants;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ArchiveServiceImpl implements ArchiveService {

    public static final Logger logger = LoggerFactory.getLogger(ArchiveServiceImpl.class);

    private Map<String, Path> fileStorage = new HashMap<>();

    @Override
    public File archive(File file, String fileName, String controlSum) {
        File zipFile = null;
        try {
            Path tempDirectory = Files.createTempDirectory(Constants.directoryTempPrefix);
            zipFile = tempDirectory.resolve(controlSum.concat(Constants.zipExtension)).toFile();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        if (zipFile == null) {
            return null; // TODO Return exception. ZipFileCreationException
        }
        try (InputStream inputStream = new FileInputStream(file);
             OutputStream outputStream = new FileOutputStream(zipFile);
             var zipOutputStream = new ZipOutputStream(outputStream)) {
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            IOUtils.copy(inputStream, zipOutputStream);
            zipOutputStream.closeEntry();
            fileStorage.put(controlSum, zipFile.toPath());
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage() + " FileNotFoundException", e);
            return null; // TODO Return exception. FileNotFoundException
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null; // TODO Return exception. ZipIOException
        }
        return zipFile;
    }
}
