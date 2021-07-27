package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.FollowRecord;
import com.privateboat.forum.backend.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowRecordRepository {
    Page<FollowRecord> getFollowRecords(Long userId, Pageable pageable);

    void save(FollowRecord followRecord);

    Boolean isFollowing(Long fromUserId, Long toUserId);
}
