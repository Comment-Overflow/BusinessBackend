package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.response.UserCardInfoDTO;
import com.privateboat.forum.backend.entity.FollowRecord;
import com.privateboat.forum.backend.exception.UserInfoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowRecordService {
    Page<FollowRecord> getFollowingNotifications(Long userId, Pageable pageable) throws UserInfoException;

    Page<UserCardInfoDTO> getFollowingRecords(Long userId, Pageable pageable) throws UserInfoException;

    Page<UserCardInfoDTO> getFollowedRecords(Long userId, Pageable pageable) throws UserInfoException;

    void postFollowRecord(Long fromUserId, Long toUserId) throws UserInfoException;

    void deleteFollowRecord(Long fromUserId, Long toUserId);
}
