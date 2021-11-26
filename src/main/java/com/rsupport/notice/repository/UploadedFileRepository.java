package com.rsupport.notice.repository;

import com.rsupport.notice.entity.UploadedFile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UploadedFileRepository extends CrudRepository<UploadedFile, Long> {

    Optional<UploadedFile> findByFileHashString(String hash);

}
