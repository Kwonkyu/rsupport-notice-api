package com.rsupport.notice.service;

import com.rsupport.notice.controller.bind.PostInformationRequest;
import com.rsupport.notice.dto.NoticePostDTO;
import com.rsupport.notice.entity.NoticePost;
import com.rsupport.notice.entity.NoticePostHit;
import com.rsupport.notice.exception.PostNotFoundException;
import com.rsupport.notice.repository.NoticePostHitRepository;
import com.rsupport.notice.repository.NoticePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicNoticePostService {

    private final NoticePostRepository noticePostRepository;
    private final NoticePostHitRepository noticePostHitRepository;

    private NoticePostHit createNoticePostHitCache(NoticePost noticePost) {
        return new NoticePostHit(noticePost.getId(), noticePost.getHit(), LocalDateTime.now());
    }

    private NoticePostHit findNoticePostHit(NoticePost noticePost) {
        Optional<NoticePostHit> byId = noticePostHitRepository.findById(noticePost.getId());
        if(byId.isEmpty()){
            return noticePostHitRepository.save(createNoticePostHitCache(noticePost));
        } else {
            return byId.get();
        }
    }

    public NoticePostDTO createPost(PostInformationRequest request) {
        NoticePost savedNotice = noticePostRepository.save(NoticePost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .noticedFrom(request.getNoticedFrom())
                .noticedUntil(request.getNoticedUntil())
                .build());
        createNoticePostHitCache(savedNotice);
        return new NoticePostDTO(savedNotice);
    }

    public List<NoticePostDTO> listPostsByPage(Pageable pageable) {
        return noticePostRepository.findAll(pageable).map(noticePost ->
                new NoticePostDTO(noticePost, findNoticePostHit(noticePost).getHit())).toList();
    }

    public NoticePostDTO findPost(long postId) {
        NoticePost noticePost = noticePostRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        NoticePostHit noticePostHit = findNoticePostHit(noticePost);
        noticePostHit.increaseHit();
        if (noticePostHit.isOutdated()) noticePost.writeBackHit(noticePostHit);
        noticePostHitRepository.save(noticePostHit);
        return new NoticePostDTO(noticePost, noticePostHit.getHit());
    }

    public NoticePostDTO updatePost(long id, PostInformationRequest request) {
        NoticePost noticePost = noticePostRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        noticePost.changeTitle(request.getTitle());
        noticePost.changeContent(request.getContent());
        noticePost.changeNoticedFrom(request.getNoticedFrom());
        noticePost.changeNoticedUntil(request.getNoticedUntil());
        return new NoticePostDTO(noticePost, findNoticePostHit(noticePost).getHit());
    }

    public void deletePost(long postId) {
        noticePostRepository.deleteById(postId);
        noticePostHitRepository.deleteById(postId);
    }

}
