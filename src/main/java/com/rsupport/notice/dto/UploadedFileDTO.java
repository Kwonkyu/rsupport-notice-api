package com.rsupport.notice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rsupport.notice.entity.UploadedFile;
import lombok.Getter;

@Getter
public class UploadedFileDTO {

    @JsonProperty("fileIdentificationString")
    private final String fileHash;

    @JsonProperty("originalFilename")
    private final String filename;

    public UploadedFileDTO(UploadedFile file) {
        this.fileHash = file.getFileHashString();
        this.filename = file.getFilename();
    }

}
