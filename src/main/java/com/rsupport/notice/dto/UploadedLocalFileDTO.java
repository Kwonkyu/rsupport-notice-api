package com.rsupport.notice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rsupport.notice.entity.UploadedLocalFile;
import lombok.Getter;

@Getter
public class UploadedLocalFileDTO {

    @JsonProperty("fileIdentificationString")
    private final String fileHash;

    @JsonProperty("originalFilename")
    private final String filename;

    public UploadedLocalFileDTO(UploadedLocalFile file) {
        this.fileHash = file.getFileHashString();
        this.filename = file.getFilename();
    }

}
