package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.recordreceive.ReplyRecordReceiveDTO;
import com.privateboat.forum.backend.entity.ReplyRecord;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReplyRecordService {
    Page<ReplyRecord> getReplyRecords(Long toUserId, Pageable pageable);
    void postReplyRecord(Long fromUserId, ReplyRecordReceiveDTO replyRecordReceiveDTO) throws UserInfoException, PostException;
}
