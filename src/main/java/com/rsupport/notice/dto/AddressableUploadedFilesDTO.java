package com.rsupport.notice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rsupport.notice.entity.UploadedFile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AddressableUploadedFilesDTO {

    @JsonProperty("uploadedFiles")
    private final List<AddressableUploadedFileDTO> uploadedFiles = new ArrayList<>();

    public AddressableUploadedFilesDTO(List<UploadedFile> files) {
        files.forEach(file -> uploadedFiles.add(new AddressableUploadedFileDTO(file)));
    }
}
