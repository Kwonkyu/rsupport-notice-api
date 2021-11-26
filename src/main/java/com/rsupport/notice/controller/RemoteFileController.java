package com.rsupport.notice.controller;

import com.rsupport.notice.controller.bind.ApiResponse;
import com.rsupport.notice.dto.AddressableUploadedFileDTO;
import com.rsupport.notice.dto.AddressableUploadedFilesDTO;
import com.rsupport.notice.service.CloudinaryUploadedFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/files")
public class RemoteFileController {

    private final CloudinaryUploadedFileService fileService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddressableUploadedFilesDTO>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        AddressableUploadedFilesDTO uploadFiles = fileService.uploadFiles(files);
        return uploadFiles.getUploadedFiles().size() == files.size() ?
                ResponseEntity.ok(ApiResponse.success(uploadFiles)) :
                ResponseEntity.accepted().body(ApiResponse.fail(uploadFiles, "Some files are not uploaded."));
    }

    @GetMapping("/{fileHash}")
    public ResponseEntity<ApiResponse<AddressableUploadedFileDTO>> getFile(@PathVariable("fileHash") String fileHash) {
        AddressableUploadedFileDTO fileByHash = fileService.getFileByHash(fileHash);
        return ResponseEntity.ok(ApiResponse.success(fileByHash));
    }

}
