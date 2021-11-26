package com.rsupport.notice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;
import com.cloudinary.utils.ObjectUtils;
import com.rsupport.notice.dto.AddressableUploadedFileDTO;
import com.rsupport.notice.dto.AddressableUploadedFilesDTO;
import com.rsupport.notice.entity.NoticePost;
import com.rsupport.notice.entity.UploadedFile;
import com.rsupport.notice.exception.FileNotFoundException;
import com.rsupport.notice.exception.PostNotFoundException;
import com.rsupport.notice.repository.NoticePostRepository;
import com.rsupport.notice.repository.UploadedFileRepository;
import com.rsupport.notice.util.UploadedFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CloudinaryUploadedFileService {

    private final UploadedFileRepository uploadedFileRepository;
    private final NoticePostRepository noticePostRepository;

    private final Cloudinary cloudinary = Singleton.getCloudinary();


    public AddressableUploadedFilesDTO uploadFiles(List<MultipartFile> files) {
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
                            File uploadingFile = UploadedFileUtil.multipartToFile(file, originalFilename);
                            Map upload = cloudinary.uploader().upload(uploadingFile, ObjectUtils.asMap(
                                    "folder", String.format("%s/", LocalDate.now()),
                                    "resource_type", "raw",
                                    "use_filename", true));
                            return uploadedFileRepository.save(new UploadedFile(
                                    hash,
                                    originalFilename,
                                    String.valueOf(upload.get("url"))));
                        } catch (IOException | RuntimeException e) {
                            log.error("File upload failed - {}. Please check cloudinary status.", e.getMessage());
                            return null;
                        }
                    });
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new AddressableUploadedFilesDTO(uploadedFiles);
    }

    public AddressableUploadedFilesDTO getAttachedFileList(long postId) {
        NoticePost noticePost = noticePostRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        return new AddressableUploadedFilesDTO(noticePost.getAttachedFiles());
    }

    public AddressableUploadedFileDTO getFileByHash(String fileHash) {
        UploadedFile uploadedFile = uploadedFileRepository.findByFileHashString(fileHash)
                .orElseThrow(() -> new FileNotFoundException(fileHash));
        return new AddressableUploadedFileDTO(uploadedFile);
    }

}
