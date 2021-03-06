package com.rsupport.notice.service;

import com.rsupport.notice.dto.UploadedFilesDTO;
import com.rsupport.notice.entity.NoticePost;
import com.rsupport.notice.entity.UploadedFile;
import com.rsupport.notice.exception.FileNotFoundException;
import com.rsupport.notice.exception.PostNotFoundException;
import com.rsupport.notice.repository.NoticePostRepository;
import com.rsupport.notice.repository.UploadedFileRepository;
import com.rsupport.notice.util.UploadedFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class LocalUploadedFileService {

    private final UploadedFileRepository uploadedFileRepository;
    private final NoticePostRepository noticePostRepository;

    private final Path uploadedFilePath;

    public LocalUploadedFileService(UploadedFileRepository uploadedFileRepository, NoticePostRepository noticePostRepository) throws IOException {
        this.uploadedFileRepository = uploadedFileRepository;
        this.noticePostRepository = noticePostRepository;
        Path localFileStoragePath = Path.of("rsupport");
        uploadedFilePath = Files.exists(localFileStoragePath) ? localFileStoragePath : Files.createDirectory(localFileStoragePath);
    }

    public UploadedFilesDTO uploadFiles(List<MultipartFile> files) {
        List<UploadedFile> uploadedFiles = files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    String hash = UploadedFileUtil.hash(file);
                    if(hash.isEmpty()) {
                        log.warn("File {} is not uploaded to the server because of hash function failure.",
                                file.getOriginalFilename());
                        return null;
                    }

                    return uploadedFileRepository.findByFileHashString(hash).orElseGet(() -> {
                        String originalFilename = Objects.requireNonNullElse(file.getOriginalFilename(), hash);
                        try {
                            file.transferTo(uploadedFilePath.resolve(originalFilename));
                            return uploadedFileRepository.save(new UploadedFile(
                                    hash,
                                    originalFilename,
                                    uploadedFilePath.resolve(originalFilename).toString()));
                        } catch (IOException e) {
                            log.error("File IO on {} failed. Please check file system authorities.",
                                    uploadedFilePath.resolve(originalFilename).toAbsolutePath());
                            return null;
                        }
                    });
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new UploadedFilesDTO(uploadedFiles);
    }

    public UploadedFilesDTO getAttachedFileList(long postId) {
        NoticePost noticePost = noticePostRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        return new UploadedFilesDTO(noticePost.getAttachedFiles());
    }

    public Path getFileByHash(String fileHash) {
        UploadedFile uploadedFile = uploadedFileRepository.findByFileHashString(fileHash)
                .orElseThrow(() -> new FileNotFoundException(fileHash));
        return Path.of(uploadedFile.getFileLocation());
    }

}
