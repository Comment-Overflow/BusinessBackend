package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.ApprovalNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApprovalNotificationRepository {
    Page<ApprovalNotification> getApprovalNotifications(Long userId, Pageable pageable);

    void postApprovalNotification(ApprovalNotification newApprovalNotification);
}
