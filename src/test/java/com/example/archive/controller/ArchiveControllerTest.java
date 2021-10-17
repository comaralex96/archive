package com.example.archive.controller;

import com.example.archive.common.Constants;
import com.example.archive.storage.ZipFileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ArchiveControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(ArchiveControllerTest.class);

    private final MockMultipartFile inputFile = new MockMultipartFile(
            "file",
            "1.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Archive me!".getBytes()
    );
    private final String inputFileETag = "03a93ec0899bccfa901a58e07099348a";

    private final MockMultipartFile emptyFile = new MockMultipartFile(
            "emptyFile",
            "empty.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "".getBytes()
    );

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ZipFileStorage zipFileStorage;

    private MockMvc mockMvc;

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Order(1)
    @DisplayName("Archive file to cache and return 200 Ok")
    public void testArchiveFileAndCache() throws Exception {
        sendRequestAndCheckResponse(HttpStatus.OK);
        deleteInputFile();
    }

    @RepeatedTest(5)
    @Order(2)
    @DisplayName("Archive file to cache N times and return 304 Not Modified")
    public void testArchiveFileNTimes(RepetitionInfo repetitionInfo) throws Exception {
        if (1 == repetitionInfo.getCurrentRepetition()) {
            sendRequestAndCheckResponse(HttpStatus.OK);
        }
        sendRequestAndCheckResponse(HttpStatus.NOT_MODIFIED);
        if (repetitionInfo.getTotalRepetitions() == repetitionInfo.getCurrentRepetition()) {
            deleteInputFile();
        }
    }

    @Test
    @DisplayName("Archive no file and return 404 Not Found")
    public void testArchiveNoFile() {
        try {
            mockMvc.perform(multipart("/zipFile"))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    @DisplayName("Archive null file and return IllegalArgumentException")
    public void testArchiveNullFile() {
        try {
            //noinspection ConstantConditions
            assertThrows(IllegalArgumentException.class, () ->
                    mockMvc.perform(multipart("/zipFile").file(null))
                            .andExpect(status().is5xxServerError()));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    @DisplayName("Archive empty file and return 400 Bad Request")
    public void testArchiveEmptyFile() {
        try {
            mockMvc.perform(multipart("/zipFile").file(emptyFile))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Disabled
    @Test
    @DisplayName("Archive null or empty file and return 404 Not Found")
    private void testArchiveNullOrEmptyFiles(HttpStatus status) {
        try {
            //noinspection ConstantConditions
            mockMvc.perform(multipart("/zipFile").file(null))
                    .andExpect(status().isNotFound());
            mockMvc.perform(multipart("/zipFile").file(emptyFile))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            fail(e);
        }
    }

    private void sendRequestAndCheckResponse(HttpStatus status) throws Exception {
        MockHttpServletResponse response = sendFile();
        assertEquals(response.getStatus(), status.value());
        compareReqRespFiles(response);
    }

    private MockHttpServletResponse sendFile() throws Exception {
        return mockMvc.perform(multipart("/zipFile").file(inputFile))
                .andDo(print())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + inputFile.getOriginalFilename() + Constants.zipExtension))
                .andExpect(header().string(HttpHeaders.ETAG, "\"" + inputFileETag + "\""))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andReturn().getResponse();
    }

    private void compareReqRespFiles(MockHttpServletResponse response) throws IOException {
        File outputFile = zipFileStorage.getFileByName(inputFileETag);
        byte[] out = Files.readAllBytes(outputFile.toPath());
        assertEquals(String.valueOf(out.length), response.getHeader(HttpHeaders.CONTENT_LENGTH));
        byte[] content = response.getContentAsByteArray();
        assertArrayEquals(out, content);
    }

    private void deleteInputFile() {
        Path workspace = (Path) ReflectionTestUtils.getField(zipFileStorage, "workspace");
        assertNotNull(workspace);
        try {
            Files.delete(workspace.resolve(inputFileETag.concat(Constants.zipExtension)).normalize());
        } catch (IOException e) {
            logger.error(e, e::getMessage);
        }
    }
}
