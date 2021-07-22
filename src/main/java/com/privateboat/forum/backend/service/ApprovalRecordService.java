package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.entity.ApprovalRecord;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApprovalRecordService {
    Page<ApprovalRecord> getApprovalRecords(Long userId, Pageable pageable);

    void postApprovalRecord(Long fromUserId, Long toUserId, Long quoteId) throws PostException, UserInfoException;
}
