package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.StarRecord;
import com.privateboat.forum.backend.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StarRecordRepository {
    Page<StarRecord> getStarRecords(Long userId, Pageable pageable);
    Page<StarRecord> getMyStarRecords(Long userId, Pageable pageable);
    void save(StarRecord starRecord);
    void deleteStarRecord(Long fromUserId, Long postId);
    Boolean checkIfHaveStarred(UserInfo userInfo, Post post);
    void setPostIsStarred(Post post, UserInfo userInfo);
}
