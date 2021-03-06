package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.FollowRecord;
import com.privateboat.forum.backend.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRecordDAO extends JpaRepository<FollowRecord, Long> {
    Page<FollowRecord> getByToUserId(Long toUserId, Pageable pageable);
    Page<FollowRecord> getByFromUserId(Long fromUserId, Pageable pageable);
    Boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);
    void deleteByFromUserIdAndToUserId(Long fromUserId, Long toUserId);
}
