package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.StarRecord;
import com.privateboat.forum.backend.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StarRecordDAO extends JpaRepository<StarRecord, Long> {
    Page<StarRecord> findByToUserId(Long userId, Pageable pageable);
    Boolean existsByFromUserAndPost(UserInfo userInfo, Post post);
    void deleteByFromUserIdAndPostId(Long fromUserId, Long postId);
}
