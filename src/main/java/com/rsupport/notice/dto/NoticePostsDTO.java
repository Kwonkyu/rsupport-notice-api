package com.rsupport.notice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rsupport.notice.entity.NoticePost;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class NoticePostsDTO {

    @JsonProperty("pageNum")
    private final int pageNum;

    @JsonProperty("pageSize")
    private final int pageSize;

    @JsonProperty("totalPages")
    private final int totalPages;

    @JsonProperty("posts")
    private final List<NoticePostDTO> noticePostDTOS = new ArrayList<>();

    public NoticePostsDTO(List<NoticePost> posts, int pageNum, int pageSize, int totalPages) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        posts.forEach(post -> noticePostDTOS.add(new NoticePostDTO(post)));
    }

}
