package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.ApprovalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRecordDAO extends JpaRepository<ApprovalRecord, Long> {
    Page<ApprovalRecord> findByToUserId(Long userId, Pageable pageable);
}
