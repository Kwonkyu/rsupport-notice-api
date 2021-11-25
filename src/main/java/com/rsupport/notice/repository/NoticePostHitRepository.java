package com.rsupport.notice.repository;

import com.rsupport.notice.entity.NoticePostHit;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticePostHitRepository extends KeyValueRepository<NoticePostHit, Long> {
}
