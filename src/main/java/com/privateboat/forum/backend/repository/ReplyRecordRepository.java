package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.ReplyRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReplyRecordRepository {
    Page<ReplyRecord> getReplyRecords(Long toUserId, Pageable pageable);
    void save(ReplyRecord replyRecord);
    void deleteReplyRecord(Long fromUserId, Long commentId);
}
