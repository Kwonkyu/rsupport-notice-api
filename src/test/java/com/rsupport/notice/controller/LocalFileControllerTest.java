package com.rsupport.notice.controller;

import com.rsupport.notice.controller.bind.PostInformationRequest;
import com.rsupport.notice.dto.NoticePostDTO;
import com.rsupport.notice.dto.UploadedFilesDTO;
import com.rsupport.notice.service.BasicNoticePostService;
import com.rsupport.notice.service.LocalUploadedFileService;
import com.rsupport.notice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
class LocalFileControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    LocalUploadedFileService fileService;
    @Autowired
    BasicNoticePostService noticePostService;
    @Autowired
    JwtUtil jwtUtil;

    String fileHash1;
    MockMultipartFile mockFile1;
    MockMultipartFile mockFile2;
    NoticePostDTO postDTO;
    String accessToken;

    @BeforeEach
    void init() {
        mockFile1 = new MockMultipartFile(
                "files",
                "mock_file1.txt",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "Hello World".getBytes());
        mockFile2 = new MockMultipartFile(
                "files",
                "mock_file2.txt",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "Cruel World".getBytes());
        UploadedFilesDTO files = fileService.uploadFiles(List.of(mockFile1));
        fileHash1 = files.getUploadedFileHashes().get(0).getFileHash();

        PostInformationRequest request = new PostInformationRequest();
        request.setTitle("TITLE");
        request.setContent("TITLE");
        request.setNoticedFrom(LocalDateTime.now());
        request.setNoticedUntil(LocalDateTime.now().plusDays(3));
        request.setAttachedFileHashes(List.of(fileHash1));
        postDTO = noticePostService.createPost(request);

        accessToken = jwtUtil.issueJwt("admin").getAccessToken();
    }

    @Test
    @DisplayName("Try to upload file without authentication.")
    void tryUploadFiles() throws Exception {
        mockMvc.perform(multipart("/api/v1/files")
                .file(mockFile2))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Upload files.")
    void uploadFiles() throws Exception {
        mockMvc.perform(multipart("/api/v1/files")
                .file(mockFile2)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.uploadedFiles").isNotEmpty());
    }

    @Test
    @DisplayName("Try download file with bad request.")
    void tryDownloadFile() throws Exception {
        mockMvc.perform(get("/api/v1/files/{fileHash}", "UNKNOWN_HASH"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Download file.")
    void downloadFile() throws Exception {
        mockMvc.perform(get("/api/v1/files/{fileHash}", fileHash1))
                .andExpect(status().isOk())
                .andExpect(content().bytes(mockFile1.getBytes()));
    }

}
