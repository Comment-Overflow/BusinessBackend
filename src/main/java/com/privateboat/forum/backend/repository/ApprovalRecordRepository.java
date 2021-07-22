package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.ApprovalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApprovalRecordRepository {
    Page<ApprovalRecord> getApprovalRecords(Long userId, Pageable pageable);

    void postApprovalRecord(ApprovalRecord newApprovalRecord);
}
