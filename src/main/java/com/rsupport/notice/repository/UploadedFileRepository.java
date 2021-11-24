package com.rsupport.notice.repository;

import com.rsupport.notice.entity.UploadedLocalFile;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UploadedFileRepository extends CrudRepository<UploadedLocalFile, Long> {

    Optional<UploadedLocalFile> findByFileHashString(String hash);

}
