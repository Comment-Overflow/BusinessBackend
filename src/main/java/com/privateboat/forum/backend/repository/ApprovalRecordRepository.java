package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.ApprovalRecord;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApprovalRecordRepository {
    Page<ApprovalRecord> getApprovalRecords(Long userId, Pageable pageable);

    void deleteApprovalRecord(Long userId, Long commentId);

    void save(ApprovalRecord newApprovalRecord);

    void saveAndFlush(ApprovalRecord newApprovalRecord);

    ApprovalStatus checkIfHaveApproved(UserInfo userInfo, Comment comment);
}
