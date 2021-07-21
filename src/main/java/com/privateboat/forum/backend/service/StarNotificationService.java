package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.entity.StarNotification;
import com.privateboat.forum.backend.exception.UserInfoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StarNotificationService {
    Page<StarNotification> getStarNotifications(Long userId, Pageable pageable);

    void postStarNotification(Long fromUserId, Long toUserId, Long postId) throws UserInfoException;
}
