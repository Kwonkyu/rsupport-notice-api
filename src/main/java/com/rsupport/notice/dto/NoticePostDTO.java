package com.rsupport.notice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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

    @JsonProperty("postNoticedFrom")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime noticedFrom;

    @JsonProperty("postNoticedUntil")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime noticedUntil;

    @JsonProperty("views")
    private final int hit;

}