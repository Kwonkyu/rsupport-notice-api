package com.rsupport.notice.controller;

import com.rsupport.notice.controller.bind.ApiResponse;
import com.rsupport.notice.dto.UploadedLocalFilesDTO;
import com.rsupport.notice.service.LocalUploadedFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final LocalUploadedFileService fileService;

    @PostMapping
    public ResponseEntity<ApiResponse<UploadedLocalFilesDTO>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        UploadedLocalFilesDTO uploadedLocalFilesDTO = fileService.uploadFiles(files);
        return ResponseEntity.ok(ApiResponse.success(uploadedLocalFilesDTO));
    }

    @GetMapping("/{fileHash}")
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable("fileHash") String fileHash) {
        Path file = fileService.getFileByHash(fileHash);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.builder("attachment")
                                .filename(file.getFileName().toString(), StandardCharsets.UTF_8)
                                .build().toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(file));
    }

}
