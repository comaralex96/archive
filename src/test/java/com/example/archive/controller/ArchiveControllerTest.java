package com.example.archive.controller;

import com.example.archive.common.Constants;
import com.example.archive.storage.ZipFileStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.nio.file.Files;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ArchiveControllerTest {

    public static final Logger logger = LoggerFactory.getLogger(ArchiveControllerTest.class);

    private final MockMultipartFile inputFile = new MockMultipartFile(
            "file",
            "1.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Archive me!".getBytes()
    );
    private final String inputFileETag = "03a93ec0899bccfa901a58e07099348a";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ZipFileStorage zipFileStorage;

    @Test
    @DisplayName("Archive file to cache and return 200 Ok")
    public void testArchiveFileAndCache() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockHttpServletResponse response = mockMvc.perform(multipart("/zipFile").file(inputFile))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + inputFile.getOriginalFilename() + Constants.zipExtension))
                .andExpect(header().string(HttpHeaders.ETAG, "\"" + inputFileETag + "\""))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andReturn().getResponse();
        File outputFile = zipFileStorage.getFileByName(inputFileETag);
        byte[] out = Files.readAllBytes(outputFile.toPath());
        Assertions.assertEquals(String.valueOf(out.length), response.getHeader(HttpHeaders.CONTENT_LENGTH));
        byte[] content = response.getContentAsByteArray();
        Assertions.assertArrayEquals(out, content);
    }
}
