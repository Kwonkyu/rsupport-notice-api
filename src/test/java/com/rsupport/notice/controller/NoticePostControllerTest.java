package com.rsupport.notice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsupport.notice.controller.bind.PostInformationRequest;
import com.rsupport.notice.entity.NoticePost;
import com.rsupport.notice.entity.UploadedFile;
import com.rsupport.notice.repository.NoticePostHitRepository;
import com.rsupport.notice.repository.NoticePostRepository;
import com.rsupport.notice.repository.UploadedFileRepository;
import com.rsupport.notice.service.BasicNoticePostService;
import com.rsupport.notice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
class NoticePostControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    BasicNoticePostService basicNoticePostService;
    @Autowired
    NoticePostRepository noticePostRepository;
    @Autowired
    UploadedFileRepository uploadedFileRepository;
    @Autowired
    NoticePostHitRepository noticePostHitRepository;
    @Autowired
    JwtUtil jwtUtil;

    UploadedFile file1;
    UploadedFile file2;
    NoticePost noticePost;
    String accessToken;

    @BeforeEach
    void init() {
        basicNoticePostService = new BasicNoticePostService(noticePostRepository, noticePostHitRepository, uploadedFileRepository);
        file1 = uploadedFileRepository.save(new UploadedFile("FILE_HASH_STRING_1", "filename1", "location1"));
        file2 = uploadedFileRepository.save(new UploadedFile("FILE_HASH_STRING_2", "filename2", "location2"));
        noticePost = NoticePost.builder()
                .title("TITLE")
                .content("CONTENT")
                .noticedFrom(LocalDateTime.now())
                .noticedUntil(LocalDateTime.now().plusDays(5))
                .build();
        noticePost.addFile(file1);
        noticePost = noticePostRepository.save(noticePost);
        accessToken = jwtUtil.issueJwt("admin").getAccessToken();
    }

    @Test
    @DisplayName("Try create post with bad request.")
    void tryCreatePost() throws Exception {
        PostInformationRequest request = new PostInformationRequest();
        request.setTitle("CREATED_POST");
        request.setContent("CREATED_CONTENT");
        request.setNoticedFrom(LocalDateTime.now());
        request.setNoticedUntil(LocalDateTime.now().plusWeeks(3));
        request.setAttachedFileHashes(List.of(file1.getFileHashString()));

        request.setTitle("");
        mockMvc.perform(post("/api/v1/notice")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        request.setTitle("CREATED_POST");

        request.setContent("");
        mockMvc.perform(post("/api/v1/notice")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        request.setContent("CREATED_CONTENT");

        request.setNoticedFrom(null);
        mockMvc.perform(post("/api/v1/notice")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        request.setNoticedFrom(LocalDateTime.now());

        request.setNoticedUntil(null);
        mockMvc.perform(post("/api/v1/notice")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        request.setNoticedUntil(LocalDateTime.now().plusWeeks(3));

        request.setAttachedFileHashes(null);
        mockMvc.perform(post("/api/v1/notice")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        request.setAttachedFileHashes(List.of(file1.getFileHashString()));

        mockMvc.perform(post("/api/v1/notice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Create post")
    void createPost() throws Exception {
        PostInformationRequest request = new PostInformationRequest();
        request.setTitle("CREATED_POST");
        request.setContent("CREATED_CONTENT");
        request.setNoticedFrom(LocalDateTime.now());
        request.setNoticedUntil(LocalDateTime.now().plusWeeks(3));
        request.setAttachedFileHashes(List.of(file1.getFileHashString()));

        mockMvc.perform(post("/api/v1/notice")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.result.postId").isNumber())
                .andExpect(jsonPath("$.result.postTitle").value(request.getTitle()))
                .andExpect(jsonPath("$.result.postContent").value(request.getContent()))
                .andExpect(jsonPath("$.result.createdDateTime").isNotEmpty())
                .andExpect(jsonPath("$.result.postNoticedFrom")
                        .value(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(request.getNoticedFrom())))
                .andExpect(jsonPath("$.result.postNoticedUntil")
                        .value(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(request.getNoticedUntil())))
                .andExpect(jsonPath("$.result.views").value(0))
                .andExpect(jsonPath("$.result.attachedFiles").isNotEmpty());
    }

    @Test
    @DisplayName("Try update post with bad request.")
    void tryUpdatePost() throws Exception {
        PostInformationRequest request = new PostInformationRequest();
        request.setTitle("UPDATED_POST");
        request.setContent("UPDATED_CONTENT");
        request.setNoticedFrom(LocalDateTime.now());
        request.setNoticedUntil(LocalDateTime.now().plusWeeks(3));
        request.setAttachedFileHashes(List.of(file2.getFileHashString()));

        request.setTitle("");
        mockMvc.perform(put("/api/v1/notice/{postId}", noticePost.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        request.setTitle("UPDATED_POST");

        request.setContent("");
        mockMvc.perform(put("/api/v1/notice/{postId}", noticePost.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        request.setContent("UPDATED_CONTENT");

        request.setNoticedFrom(null);
        mockMvc.perform(put("/api/v1/notice/{postId}", noticePost.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        request.setNoticedFrom(LocalDateTime.now());

        request.setNoticedUntil(null);
        mockMvc.perform(put("/api/v1/notice/{postId}", noticePost.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        request.setNoticedUntil(LocalDateTime.now().plusWeeks(3));

        request.setAttachedFileHashes(null);
        mockMvc.perform(put("/api/v1/notice/{postId}", noticePost.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        request.setAttachedFileHashes(List.of(file1.getFileHashString()));

        mockMvc.perform(put("/api/v1/notice/{postId}", noticePost.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update post")
    void updatePost() throws Exception {
        PostInformationRequest request = new PostInformationRequest();
        request.setTitle("UPDATED_POST");
        request.setContent("UPDATED_CONTENT");
        request.setNoticedFrom(LocalDateTime.now());
        request.setNoticedUntil(LocalDateTime.now().plusWeeks(3));
        request.setAttachedFileHashes(List.of(file1.getFileHashString()));

        mockMvc.perform(put("/api/v1/notice/{postId}", noticePost.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.postId").value(noticePost.getId()))
                .andExpect(jsonPath("$.result.postTitle").value(request.getTitle()))
                .andExpect(jsonPath("$.result.postContent").value(request.getContent()))
                .andExpect(jsonPath("$.result.createdDateTime").isNotEmpty())
                .andExpect(jsonPath("$.result.postNoticedFrom")
                        .value(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(request.getNoticedFrom())))
                .andExpect(jsonPath("$.result.postNoticedUntil")
                        .value(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(request.getNoticedUntil())))
                .andExpect(jsonPath("$.result.views").isNumber())
                .andExpect(jsonPath("$.result.attachedFiles").isNotEmpty());
    }

    @Test
    @DisplayName("Try delete post with bad request.")
    void tryDeletePost() throws Exception {
        mockMvc.perform(delete("/api/v1/notice/{postId}", noticePost.getId()))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/v1/notice/{postId}", 9999L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Delete post")
    void deletePost() throws Exception {
        mockMvc.perform(delete("/api/v1/notice/{postId}", noticePost.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNoContent());
        assertTrue(noticePostRepository.findById(noticePost.getId()).isEmpty());
    }

    @Test
    @DisplayName("Try to get attached file's list with bad request.")
    void tryGetAttachedFiles() throws Exception {
        mockMvc.perform(get("/api/v1/notice/{postId}/files", 9999L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get attached file's list.")
    void getAttachedFiles() throws Exception {
        mockMvc.perform(get("/api/v1/notice/{postId}/files", noticePost.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.uploadedFiles").isArray())
                .andExpect(jsonPath("$.result.uploadedFiles[0].fileIdentificationString").value(file1.getFileHashString()))
                .andExpect(jsonPath("$.result.uploadedFiles[0].originalFilename").value(file1.getFilename()));
    }

}
