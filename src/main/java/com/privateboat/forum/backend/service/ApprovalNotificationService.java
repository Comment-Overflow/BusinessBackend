package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.entity.ApprovalNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApprovalNotificationService {
    Page<ApprovalNotification> getApprovalNotifications(Long userId, Pageable pageable);

    void postApprovalNotification(Long fromUserId, Long toUserId, Long quoteId);
}
