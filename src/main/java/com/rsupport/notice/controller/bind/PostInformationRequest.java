package com.rsupport.notice.controller.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostInformationRequest {

    @JsonProperty("postTitle")
    @NotBlank
    @Length(max = 255)
    private String title = "";

    @JsonProperty("postContent")
    @NotBlank
    @Length(max = 65535)
    private String content = "";

    @JsonProperty("postNoticedFrom")
    @NotNull
    private LocalDateTime noticedFrom = LocalDateTime.now();

    @JsonProperty("postNoticedUntil")
    @NotNull
    private LocalDateTime noticedUntil = LocalDateTime.MAX;

    @JsonProperty("attachedFiles")
    @NotNull
    private List<String> attachedFileHashes = new ArrayList<>();

}
