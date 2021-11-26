package com.rsupport.notice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rsupport.notice.entity.UploadedLocalFile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UploadedLocalFilesDTO {

    @JsonProperty("uploadedFiles")
    private final List<UploadedLocalFileDTO> uploadedFileHashes = new ArrayList<>();

    public UploadedLocalFilesDTO(List<UploadedLocalFile> files) {
        files.forEach(uploadedFile ->
                uploadedFileHashes.add(new UploadedLocalFileDTO(uploadedFile)));
    }

}
