package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.FollowRecord;
import com.privateboat.forum.backend.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowRecordRepository {
    Page<FollowRecord> getFollowRecords(Long userId, Pageable pageable);

    void postFollowRecord(FollowRecord followRecord);

    Boolean isMutualFollowed(Long userId, UserInfo userInfo);
}