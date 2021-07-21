package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.ApprovalNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalNotificationDAO extends JpaRepository<ApprovalNotification, Long> {
    Page<ApprovalNotification> findByToUserId(Long userId, Pageable pageable);
}
