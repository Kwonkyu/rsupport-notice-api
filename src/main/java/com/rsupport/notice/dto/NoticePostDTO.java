package com.rsupport.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rsupport.notice.entity.NoticePost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class NoticePostDTO {

    @JsonProperty("postId")
    private final long id;

    @JsonProperty("postTitle")
    private final String title;

    @JsonProperty("postContent")
    private final String content;

    @JsonProperty("createdDateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    @JsonProperty("postNoticedFrom")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime noticedFrom;

    @JsonProperty("postNoticedUntil")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime noticedUntil;

    @JsonProperty("views")
    private final int hit;

    public NoticePostDTO(NoticePost post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.noticedFrom = post.getNoticedFrom();
        this.noticedUntil = post.getNoticedUntil();
        this.hit = post.getHit();
    }

    public NoticePostDTO(NoticePost post, int hit) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.noticedFrom = post.getNoticedFrom();
        this.noticedUntil = post.getNoticedUntil();
        this.hit = hit;
    }

}
