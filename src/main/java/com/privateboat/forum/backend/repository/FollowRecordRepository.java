package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.FollowRecord;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.FollowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowRecordRepository {
    Page<FollowRecord> getFollowingRecords(Long userId, Pageable pageable);

    Page<FollowRecord> getFollowedRecords(Long userId, Pageable pageable);

    void save(FollowRecord followRecord);

    FollowStatus getFollowStatus(Long fromUserId, Long toUserId);

    void deleteFollowRecord(Long fromUserId, Long toUserId);
}
