package com.rsupport.notice.controller;

import com.rsupport.notice.controller.bind.ApiResponse;
import com.rsupport.notice.controller.bind.PostInformationRequest;
import com.rsupport.notice.dto.NoticePostDTO;
import com.rsupport.notice.service.BasicNoticePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v1/notice")
public class NoticePostController {

    private final BasicNoticePostService noticePostService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticePostDTO>>> listPosts(Pageable pageable) { // page, size, sort params.
        List<NoticePostDTO> noticePostDTOS = noticePostService.listPostsByPage(pageable);
        return ResponseEntity.ok(ApiResponse.success(noticePostDTOS));
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
                .created(URI.create("/api/v1/notice/" + post.getId()))
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

}
