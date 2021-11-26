package com.rsupport.notice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rsupport.notice.entity.UploadedFile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UploadedFilesDTO {

    @JsonProperty("uploadedFiles")
    private final List<UploadedFileDTO> uploadedFileHashes = new ArrayList<>();

    public UploadedFilesDTO(List<UploadedFile> files) {
        files.forEach(uploadedFile ->
                uploadedFileHashes.add(new UploadedFileDTO(uploadedFile)));
    }

}
