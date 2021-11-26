package com.rsupport.notice.service;

import com.rsupport.notice.controller.bind.PostInformationRequest;
import com.rsupport.notice.dto.NoticePostDTO;
import com.rsupport.notice.dto.UploadedFileDTO;
import com.rsupport.notice.dto.UploadedFilesDTO;
import com.rsupport.notice.exception.FileNotFoundException;
import com.rsupport.notice.exception.PostNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LocalFileServiceTest {

    @Autowired
    LocalUploadedFileService fileService;
    @Autowired BasicNoticePostService noticePostService;

    String fileHash1;
    String fileHash2;
    MockMultipartFile mockFile1;
    MockMultipartFile mockFile2;
    NoticePostDTO postDTO;

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
        UploadedFilesDTO files = fileService.uploadFiles(List.of(mockFile1, mockFile2));
        fileHash1 = files.getUploadedFileHashes().get(0).getFileHash();
        fileHash2 = files.getUploadedFileHashes().get(1).getFileHash();

        PostInformationRequest request = new PostInformationRequest();
        request.setTitle("TITLE");
        request.setContent("TITLE");
        request.setNoticedFrom(LocalDateTime.now());
        request.setNoticedUntil(LocalDateTime.now().plusDays(3));
        request.setAttachedFileHashes(List.of(fileHash1));
        postDTO = noticePostService.createPost(request);
    }

    @Test
    @DisplayName("Try get file using unknown id.")
    void tryGetFile() {
        assertThrows(FileNotFoundException.class, () -> fileService.getFileByHash("UNKNOWN_HASH"));
    }

    @Test
    @DisplayName("Get file.")
    void getFile() {
        Path fileByHash = fileService.getFileByHash(fileHash1);
        assertEquals(mockFile1.getOriginalFilename(), fileByHash.getFileName().toString());
    }

    @Test
    @DisplayName("Try get attached files of not existing notice.")
    void tryGetAttachedFiles() {
        assertThrows(PostNotFoundException.class, () -> fileService.getAttachedFileList(9999L));
    }

    @Test
    @DisplayName("Get attached files of notice.")
    void getAttachedFiles() {
        UploadedFilesDTO attachedFileList = fileService.getAttachedFileList(postDTO.getId());
        assertFalse(attachedFileList.getUploadedFileHashes().isEmpty());
        UploadedFileDTO file = attachedFileList.getUploadedFileHashes().get(0);
        assertEquals(fileHash1, file.getFileHash());
        assertEquals(mockFile1.getOriginalFilename(), file.getFilename());
    }

    @Test
    @DisplayName("Try add new files which doesn't exist when updating notice.")
    void tryAttachNewFiles() {
        long postId = postDTO.getId();
        PostInformationRequest request = new PostInformationRequest();
        request.setTitle("TITLE");
        request.setContent("CONTENT");
        request.setNoticedFrom(LocalDateTime.now());
        request.setNoticedUntil(LocalDateTime.now().plusDays(1));
        request.setAttachedFileHashes(List.of("UNKNOWN_FILE_HASH"));

        NoticePostDTO noticePostDTO = noticePostService.updatePost(postId, request);
        assertTrue(noticePostDTO.getUploadedFilesDTO().getUploadedFileHashes().isEmpty());
    }

    @Test
    @DisplayName("Add new files when updating notice.")
    void attachNewFiles() {
        PostInformationRequest request = new PostInformationRequest();
        request.setTitle("TITLE");
        request.setContent("CONTENT");
        request.setNoticedFrom(LocalDateTime.now());
        request.setNoticedUntil(LocalDateTime.now().plusDays(1));
        request.setAttachedFileHashes(List.of(fileHash1, fileHash2));

        noticePostService.updatePost(postDTO.getId(), request);

        UploadedFilesDTO attachedFileList = fileService.getAttachedFileList(postDTO.getId());
        assertFalse(attachedFileList.getUploadedFileHashes().isEmpty());
        assertEquals(2, attachedFileList.getUploadedFileHashes().size());
        assertTrue(attachedFileList.getUploadedFileHashes().stream()
                .anyMatch(fileDTO -> fileDTO.getFileHash().equals(fileHash1)));
        assertTrue(attachedFileList.getUploadedFileHashes().stream()
                .anyMatch(fileDTO -> fileDTO.getFileHash().equals(fileHash2)));
    }

    @Test
    @DisplayName("Remove existing files when updating notice.")
    void detachExistingFiles() {
        PostInformationRequest request = new PostInformationRequest();
        request.setTitle("TITLE");
        request.setContent("CONTENT");
        request.setNoticedFrom(LocalDateTime.now());
        request.setNoticedUntil(LocalDateTime.now().plusDays(1));
        request.setAttachedFileHashes(List.of(fileHash2));

        noticePostService.updatePost(postDTO.getId(), request);

        UploadedFilesDTO attachedFileList = fileService.getAttachedFileList(postDTO.getId());
        assertFalse(attachedFileList.getUploadedFileHashes().isEmpty());
        assertEquals(1, attachedFileList.getUploadedFileHashes().size());
        assertTrue(attachedFileList.getUploadedFileHashes().stream()
                .noneMatch(fileDTO -> fileDTO.getFileHash().equals(fileHash1)));
        assertTrue(attachedFileList.getUploadedFileHashes().stream()
                .anyMatch(fileDTO -> fileDTO.getFileHash().equals(fileHash2)));
    }

}
