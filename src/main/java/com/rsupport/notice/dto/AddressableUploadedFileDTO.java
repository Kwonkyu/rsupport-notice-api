package com.rsupport.notice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rsupport.notice.entity.UploadedFile;
import lombok.Getter;

@Getter
public class AddressableUploadedFileDTO extends UploadedFileDTO {

    @JsonProperty("fileLocation")
    private final String fileLocation;

    public AddressableUploadedFileDTO(UploadedFile file) {
        super(file);
        this.fileLocation = file.getFileLocation();
    }

}
