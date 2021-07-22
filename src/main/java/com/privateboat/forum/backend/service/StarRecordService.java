package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.entity.StarRecord;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StarRecordService {
    Page<StarRecord> getStarRecords(Long userId, Pageable pageable);

    void postStarRecord(Long fromUserId, Long toUserId, Long postId) throws UserInfoException, PostException;
}
