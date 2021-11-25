package com.rsupport.notice.service;

import com.rsupport.notice.controller.bind.PostInformationRequest;
import com.rsupport.notice.dto.NoticePostDTO;
import com.rsupport.notice.dto.NoticePostsDTO;
import com.rsupport.notice.entity.NoticePost;
import com.rsupport.notice.entity.UploadedLocalFile;
import com.rsupport.notice.exception.PostNotFoundException;
import com.rsupport.notice.repository.NoticePostHitRepository;
import com.rsupport.notice.repository.NoticePostRepository;
import com.rsupport.notice.repository.UploadedFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class NoticePostServiceTest {

    @Autowired NoticePostRepository noticePostRepository;
    @Autowired UploadedFileRepository uploadedFileRepository;
    @Autowired NoticePostHitRepository noticePostHitRepository;

    BasicNoticePostService basicNoticePostService;

    UploadedLocalFile file1;
    UploadedLocalFile file2;
    NoticePost noticePost;

    @BeforeEach
    void init() {
        basicNoticePostService = new BasicNoticePostService(noticePostRepository, noticePostHitRepository, uploadedFileRepository);
        file1 = uploadedFileRepository.save(new UploadedLocalFile("FILE_HASH_STRING_1", "filename1", "location1"));
        file2 = uploadedFileRepository.save(new UploadedLocalFile("FILE_HASH_STRING_2", "filename2", "location2"));
        noticePost = NoticePost.builder()
                .title("TITLE")
                .content("CONTENT")
                .noticedFrom(LocalDateTime.now())
                .noticedUntil(LocalDateTime.now().plusDays(5))
                .build();
        noticePost.addFile(file1);
        noticePost = noticePostRepository.save(noticePost);
    }

    @Test
    @DisplayName("Try creating post with bad request.")
    void tryCreatePost() {
        PostInformationRequest request = new PostInformationRequest();
        request.setTitle("CREATED_POST");
        request.setContent("CREATED_CONTENT");
        request.setAttachedFileHashes(List.of(file1.getFileHashString()));

        request.setNoticedFrom(LocalDateTime.now().plusWeeks(3));
        request.setNoticedUntil(LocalDateTime.now());
        assertThrows(IllegalArgumentException.class, () -> basicNoticePostService.createPost(request));
        request.setNoticedFrom(LocalDateTime.now());
        request.setNoticedUntil(LocalDateTime.now().plusWeeks(3));

        request.setTitle("");
        assertThrows(IllegalArgumentException.class, () -> basicNoticePostService.createPost(request));
        request.setTitle("TITLE");

        request.setContent("");
        assertThrows(IllegalArgumentException.class, () -> basicNoticePostService.createPost(request));
        request.setContent("CONTENT");

        request.setNoticedFrom(null);
        assertThrows(IllegalArgumentException.class, () -> basicNoticePostService.createPost(request));
        request.setNoticedFrom(LocalDateTime.now());

        request.setNoticedUntil(null);
        assertThrows(IllegalArgumentException.class, () -> basicNoticePostService.createPost(request));
        request.setNoticedUntil(LocalDateTime.now().plusDays(3));

        request.setAttachedFileHashes(null);
        assertThrows(IllegalArgumentException.class, () -> basicNoticePostService.createPost(request));
        request.setAttachedFileHashes(List.of(file1.getFileHashString()));
    }

    @Test
    @DisplayName("Create post")
    void createPost() {
        PostInformationRequest request = new PostInformationRequest();
        request.setTitle("CREATED_POST");
        request.setContent("CREATED_CONTENT");
        request.setNoticedFrom(LocalDateTime.now());
        request.setNoticedUntil(LocalDateTime.now().plusWeeks(3));
        request.setAttachedFileHashes(List.of(file1.getFileHashString()));

        NoticePostDTO post = basicNoticePostService.createPost(request);

        assertEquals(request.getTitle(), post.getTitle());
        assertEquals(request.getContent(), post.getContent());
        assertNotNull(post.getCreatedAt());
        assertEquals(request.getNoticedFrom(), post.getNoticedFrom());
        assertEquals(request.getNoticedUntil(), post.getNoticedUntil());
        assertEquals(0, post.getHit());
        assertNotNull(post.getUploadedLocalFilesDTO());
        assertEquals(file1.getFileHashString(), post.getUploadedLocalFilesDTO().getUploadedFileHashes().get(0).getFileHash());
        assertEquals(file1.getFilename(), post.getUploadedLocalFilesDTO().getUploadedFileHashes().get(0).getFilename());
    }

    @Test
    @DisplayName("Try read post with bad request.")
    void tryReadPost() {
        assertThrows(PostNotFoundException.class, () -> basicNoticePostService.findPost(9999L));
    }

    @Test
    @DisplayName("Read post.")
    void readPost() {
        NoticePostDTO post = basicNoticePostService.findPost(noticePost.getId());
        assertEquals(noticePost.getTitle(), post.getTitle());
        assertEquals(noticePost.getContent(), post.getContent());
        assertEquals(noticePost.getCreatedAt(), post.getCreatedAt());
        assertEquals(noticePost.getNoticedFrom(), post.getNoticedFrom());
        assertEquals(noticePost.getNoticedUntil(), post.getNoticedUntil());
        assertEquals(noticePost.getAttachedFiles().size(), post.getUploadedLocalFilesDTO().getUploadedFileHashes().size());
        assertEquals(file1.getFileHashString(), post.getUploadedLocalFilesDTO().getUploadedFileHashes().get(0).getFileHash());
        assertEquals(file1.getFilename(), post.getUploadedLocalFilesDTO().getUploadedFileHashes().get(0).getFilename());
    }

    @Test
    @DisplayName("List posts")
    void listPosts() {
        NoticePostsDTO noticePostsDTO = basicNoticePostService.listPostsByPage(PageRequest.of(0, 1));
        assertEquals(1, noticePostsDTO.getNoticePostDTOS().size());

        PostInformationRequest request = new PostInformationRequest();
        request.setTitle("TITLE2");
        request.setContent("CONTENT2");
        request.setNoticedFrom(LocalDateTime.now().plusWeeks(1));
        request.setNoticedUntil(LocalDateTime.now().plusWeeks(5));
        request.setAttachedFileHashes(new ArrayList<>());
        basicNoticePostService.createPost(request);

        noticePostsDTO = basicNoticePostService.listPostsByPage(PageRequest.of(1, 1));
        assertEquals(1, noticePostsDTO.getNoticePostDTOS().size());
        NoticePostDTO noticePostDTO = noticePostsDTO.getNoticePostDTOS().get(0);
        assertEquals(request.getTitle(), noticePostDTO.getTitle());
        assertEquals(request.getContent(), noticePostDTO.getContent());
        assertEquals(request.getNoticedFrom(), noticePostDTO.getNoticedFrom());
        assertEquals(request.getNoticedUntil(), noticePostDTO.getNoticedUntil());
        assertTrue(request.getAttachedFileHashes().isEmpty());

        noticePostsDTO = basicNoticePostService.listPostsByPage(PageRequest.of(0, 20));
        assertEquals(2, noticePostsDTO.getNoticePostDTOS().size());
    }

    @Test
    @DisplayName("Try update post with bad request.")
    void tryUpdatePost() {
        Long postId = noticePost.getId();
        PostInformationRequest request = new PostInformationRequest();
        request.setTitle("UPDATED_POST");
        request.setContent("UPDATED_CONTENT");
        request.setAttachedFileHashes(List.of(file1.getFileHashString()));

        request.setNoticedFrom(LocalDateTime.now().plusWeeks(3));
        request.setNoticedUntil(LocalDateTime.now());
        assertThrows(IllegalArgumentException.class, () -> basicNoticePostService.updatePost(postId, request));
        request.setNoticedFrom(LocalDateTime.now());
        request.setNoticedUntil(LocalDateTime.now().plusWeeks(3));

        request.setTitle("");
        assertThrows(IllegalArgumentException.class, () -> basicNoticePostService.updatePost(postId, request));
        request.setTitle("TITLE");

        request.setContent("");
        assertThrows(IllegalArgumentException.class, () -> basicNoticePostService.updatePost(postId, request));
        request.setContent("CONTENT");

        request.setNoticedFrom(null);
        assertThrows(IllegalArgumentException.class, () -> basicNoticePostService.updatePost(postId, request));
        request.setNoticedFrom(LocalDateTime.now());

        request.setNoticedUntil(null);
        assertThrows(IllegalArgumentException.class, () -> basicNoticePostService.updatePost(postId, request));
        request.setNoticedUntil(LocalDateTime.now().plusDays(3));

        request.setAttachedFileHashes(null);
        assertThrows(IllegalArgumentException.class, () -> basicNoticePostService.updatePost(postId, request));
        request.setAttachedFileHashes(List.of(file1.getFileHashString()));
    }

    @Test
    @DisplayName("Update post")
    void updatePost() {
        PostInformationRequest request = new PostInformationRequest();
        request.setTitle("UPDATED_TITLE");
        request.setContent("UPDATED_CONTENT");
        request.setNoticedFrom(LocalDateTime.now().plusHours(3));
        request.setNoticedUntil(LocalDateTime.now().plusDays(2));
        request.setAttachedFileHashes(List.of(file2.getFileHashString()));

        NoticePostDTO noticePostDTO = basicNoticePostService.updatePost(noticePost.getId(), request);
        assertEquals(request.getTitle(), noticePostDTO.getTitle());
        assertEquals(request.getContent(), noticePostDTO.getContent());
        assertEquals(request.getNoticedFrom(), noticePostDTO.getNoticedFrom());
        assertEquals(request.getNoticedUntil(), noticePostDTO.getNoticedUntil());
        assertFalse(noticePostDTO.getUploadedLocalFilesDTO().getUploadedFileHashes().isEmpty());
        assertEquals(file2.getFileHashString(), noticePostDTO.getUploadedLocalFilesDTO().getUploadedFileHashes().get(0).getFileHash());
    }

    @Test
    @DisplayName("Try delete post with bad request.")
    void tryDeletePost() {
        assertThrows(PostNotFoundException.class, () -> basicNoticePostService.deletePost(9999L));
    }

    @Test
    @DisplayName("Delete post")
    void deletePost() {
        assertDoesNotThrow(() -> basicNoticePostService.deletePost(noticePost.getId()));
        assertFalse(noticePostRepository.existsById(noticePost.getId()));
    }

}
