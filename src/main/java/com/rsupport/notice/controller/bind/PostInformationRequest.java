package com.rsupport.notice.controller.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class PostInformationRequest {

    @JsonProperty("postTitle")
    @NotBlank
    @Length(max = 255)
    private String title;

    @JsonProperty("postContent")
    @NotBlank
    @Length(max = 65535)
    private String content;

    @JsonProperty("postNoticedFrom")
    private LocalDateTime noticedFrom = LocalDateTime.now();

    @JsonProperty("postNoticedUntil")
    private LocalDateTime noticedUntil = LocalDateTime.MAX;

}
