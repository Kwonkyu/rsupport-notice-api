package com.rsupport.notice.service;

import com.rsupport.notice.controller.bind.PostInformationRequest;
import com.rsupport.notice.dto.NoticePostDTO;
import com.rsupport.notice.entity.NoticePost;
import com.rsupport.notice.exception.PostNotFoundException;
import com.rsupport.notice.repository.NoticePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicNoticePostService {

    private final NoticePostRepository noticePostRepository;

    public NoticePostDTO createPost(PostInformationRequest request) {
        NoticePost savedNotice = noticePostRepository.save(NoticePost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .noticedFrom(request.getNoticedFrom())
                .noticedUntil(request.getNoticedUntil())
                .build());
        return new NoticePostDTO(savedNotice);
    }

    public List<NoticePostDTO> listPostsByPage(Pageable pageable) {
        return noticePostRepository.findAll(pageable).map(NoticePostDTO::new).toList();
    }

    public NoticePostDTO findPost(long postId) {
        NoticePost noticePost = noticePostRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        return new NoticePostDTO(noticePost);
    }

    public NoticePostDTO updatePost(long id, PostInformationRequest request) {
        NoticePost noticePost = noticePostRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        noticePost.changeTitle(request.getTitle());
        noticePost.changeContent(request.getContent());
        noticePost.changeNoticedFrom(request.getNoticedFrom());
        noticePost.changeNoticedUntil(request.getNoticedUntil());
        return new NoticePostDTO(noticePost);
    }

    public void deletePost(long id) {
        noticePostRepository.deleteById(id);
    }

}
