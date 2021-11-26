package com.rsupport.notice.controller;

import com.rsupport.notice.controller.bind.ApiResponse;
import com.rsupport.notice.controller.bind.PostInformationRequest;
import com.rsupport.notice.dto.AddressableUploadedFilesDTO;
import com.rsupport.notice.dto.NoticePostDTO;
import com.rsupport.notice.dto.NoticePostsDTO;
import com.rsupport.notice.service.BasicNoticePostService;
import com.rsupport.notice.service.CloudinaryUploadedFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notice")
public class NoticePostController {

    private final BasicNoticePostService noticePostService;
    private final CloudinaryUploadedFileService cloudinaryUploadedFileService;

    @GetMapping
    public ResponseEntity<ApiResponse<NoticePostsDTO>> listPosts(Pageable pageable) { // page, size, sort params.
        NoticePostsDTO noticePostsDTO = noticePostService.listPostsByPage(pageable);
        return ResponseEntity.ok(ApiResponse.success(noticePostsDTO));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<NoticePostDTO>> readPost(@PathVariable("postId") long postId) {
        NoticePostDTO post = noticePostService.findPost(postId);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NoticePostDTO>> createPost(@Valid @RequestBody PostInformationRequest request) {
        NoticePostDTO post = noticePostService.createPost(request);
        return ResponseEntity
                .created(MvcUriComponentsBuilder.fromMethodName(NoticePostController.class, "readPost", post.getId()).build(post.getId()))
                .body(ApiResponse.success(post));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<NoticePostDTO>> updatePost(@PathVariable("postId") long postId,
                                                    @Valid @RequestBody PostInformationRequest request) {
        NoticePostDTO noticePostDTO = noticePostService.updatePost(postId, request);
        return ResponseEntity.ok(ApiResponse.success(noticePostDTO));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Object>> deletePost(@PathVariable("postId") long postId) {
        noticePostService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/files")
    public ResponseEntity<ApiResponse<AddressableUploadedFilesDTO>> getAttachedFiles(@PathVariable("postId") long postId) {
        AddressableUploadedFilesDTO attachedFileList = cloudinaryUploadedFileService.getAttachedFileList(postId);
        return ResponseEntity.ok(ApiResponse.success(attachedFileList));
    }

}
