package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.ReplyRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRecordDAO extends JpaRepository<ReplyRecord, Long> {
    Page<ReplyRecord> getByToUserId(Long toUserId, Pageable pageable);
}
