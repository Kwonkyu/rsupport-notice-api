package com.rsupport.notice.controller;

import com.rsupport.notice.controller.bind.PostInformationRequest;
import com.rsupport.notice.dto.AddressableUploadedFilesDTO;
import com.rsupport.notice.dto.NoticePostDTO;
import com.rsupport.notice.service.BasicNoticePostService;
import com.rsupport.notice.service.CloudinaryUploadedFileService;
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
class RemoteFileControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    CloudinaryUploadedFileService fileService;
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
        AddressableUploadedFilesDTO files = fileService.uploadFiles(List.of(mockFile1));
        fileHash1 = files.getUploadedFiles().get(0).getFileHash();

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
        mockMvc.perform(multipart("/api/v2/files")
                .file(mockFile2))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Upload files.")
    void uploadFiles() throws Exception {
        mockMvc.perform(multipart("/api/v2/files")
                .file(mockFile2)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.uploadedFiles").isNotEmpty())
                .andExpect(jsonPath("$.result.uploadedFiles[0].fileLocation").isNotEmpty());
    }

    @Test
    @DisplayName("Try get file uri with bad request.")
    void tryGetFileUri() throws Exception {
        mockMvc.perform(get("/api/v2/files/{fileHash}", "UNKNOWN_HASH"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get file uri.")
    void getFileUri() throws Exception {
        mockMvc.perform(get("/api/v2/files/{fileHash}", fileHash1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.fileLocation").isNotEmpty());
    }

}
