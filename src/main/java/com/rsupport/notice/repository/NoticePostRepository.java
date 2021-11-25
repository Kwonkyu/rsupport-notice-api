package com.rsupport.notice.repository;

import com.rsupport.notice.entity.NoticePost;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticePostRepository extends PagingAndSortingRepository<NoticePost, Long> {
}
