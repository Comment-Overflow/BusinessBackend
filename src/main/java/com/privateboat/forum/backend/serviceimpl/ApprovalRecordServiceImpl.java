package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.entity.ApprovalRecord;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.ApprovalRecordRepository;
import com.privateboat.forum.backend.repository.CommentRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.ApprovalRecordService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@AllArgsConstructor
public class ApprovalRecordServiceImpl implements ApprovalRecordService {
    private final UserInfoRepository userInfoRepository;
    private final ApprovalRecordRepository approvalRecordRepository;
    private final CommentRepository commentRepository;

    @Override
    public Page<ApprovalRecord> getApprovalRecords(Long userId, Pageable pageable) {
        return approvalRecordRepository.getApprovalRecords(userId, pageable);
    }

    @Override
    public void postApprovalRecord(Long fromUserId, Long toUserId, Long quoteId) throws UserInfoException, PostException {
        ApprovalRecord newApprovalRecord = new ApprovalRecord();

        newApprovalRecord.setFromUser(userInfoRepository.getById(fromUserId));
        newApprovalRecord.setToUserId(toUserId);
        newApprovalRecord.setTimestamp(new Timestamp(System.currentTimeMillis()));

        newApprovalRecord.setComment(commentRepository.getById(fromUserId));
        approvalRecordRepository.postApprovalRecord(newApprovalRecord);
    }
}
