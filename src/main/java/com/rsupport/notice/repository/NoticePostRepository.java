package com.rsupport.notice.repository;

import com.rsupport.notice.entity.NoticePost;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NoticePostRepository extends PagingAndSortingRepository<NoticePost, Long> {
}
