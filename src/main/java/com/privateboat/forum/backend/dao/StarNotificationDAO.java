package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.StarNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StarNotificationDAO extends JpaRepository<StarNotification, Long> {
    Page<StarNotification> findByToUserId(Long userId, Pageable pageable);
}
