package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.request.ReplyRecordReceiveDTO;
import com.privateboat.forum.backend.dto.response.ReplyRecordDTO;
import com.privateboat.forum.backend.entity.ReplyRecord;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReplyRecordService {
    List<ReplyRecordDTO> getReplyRecords(Long toUserId, Pageable pageable);
    void postReplyRecord(Long fromUserId, ReplyRecordReceiveDTO replyRecordReceiveDTO) throws UserInfoException, PostException;
}
