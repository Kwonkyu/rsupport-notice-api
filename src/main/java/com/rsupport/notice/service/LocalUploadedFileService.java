package com.rsupport.notice.service;

import com.rsupport.notice.dto.UploadedLocalFilesDTO;
import com.rsupport.notice.entity.NoticePost;
import com.rsupport.notice.entity.UploadedLocalFile;
import com.rsupport.notice.exception.PostNotFoundException;
import com.rsupport.notice.repository.NoticePostRepository;
import com.rsupport.notice.repository.UploadedFileRepository;
import com.rsupport.notice.util.UploadedFileHashUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocalUploadedFileService {

    private final UploadedFileRepository uploadedFileRepository;
    private final NoticePostRepository noticePostRepository;

    private final UploadedFileHashUtil hashUtil;

    private final Path uploadedFilePath;

    public LocalUploadedFileService(UploadedFileRepository uploadedFileRepository, NoticePostRepository noticePostRepository, UploadedFileHashUtil hashUtil) throws IOException {
        this.uploadedFileRepository = uploadedFileRepository;
        this.noticePostRepository = noticePostRepository;
        this.hashUtil = hashUtil;
        Path localFileStoragePath = Path.of("rsupport");
        uploadedFilePath = Files.exists(localFileStoragePath) ? localFileStoragePath : Files.createDirectory(localFileStoragePath);
    }

    public UploadedLocalFilesDTO uploadFiles(List<MultipartFile> files) {
        List<UploadedLocalFile> uploadedLocalFiles = files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    try {
                        String hash = hashUtil.hash(file);
                        String originalFilename = Objects.requireNonNullElse(file.getOriginalFilename(), hash);
                        file.transferTo(uploadedFilePath.resolve(originalFilename));
                        return uploadedFileRepository.save(
                                new UploadedLocalFile(hash, originalFilename, uploadedFilePath.resolve(originalFilename).toString()));
                    } catch (IOException | NoSuchAlgorithmException e) {
                        // TODO: logging for file upload error.
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new UploadedLocalFilesDTO(uploadedLocalFiles);
    }

    public UploadedLocalFilesDTO getAttachedFileList(long postId) {
        NoticePost noticePost = noticePostRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        return new UploadedLocalFilesDTO(noticePost.getAttachedFiles());
    }

    public Path getFileByHash(String fileHash) {
        UploadedLocalFile uploadedLocalFile = uploadedFileRepository.findByFileHashString(fileHash)
                .orElseThrow(() -> new IllegalArgumentException("File not found."));
        return Path.of(uploadedLocalFile.getFileLocation());
    }

}