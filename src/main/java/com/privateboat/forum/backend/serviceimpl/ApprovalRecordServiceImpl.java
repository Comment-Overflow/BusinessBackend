package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.request.ApprovalRecordReceiveDTO;
import com.privateboat.forum.backend.entity.ApprovalRecord;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import com.privateboat.forum.backend.enumerate.RecordType;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.ApprovalRecordRepository;
import com.privateboat.forum.backend.repository.CommentRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.repository.UserStatisticRepository;
import com.privateboat.forum.backend.service.ApprovalRecordService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;

@Service
@AllArgsConstructor
@Transactional
public class ApprovalRecordServiceImpl implements ApprovalRecordService {
    private final UserInfoRepository userInfoRepository;
    private final ApprovalRecordRepository approvalRecordRepository;
    private final CommentRepository commentRepository;
    private final UserStatisticRepository userStatisticRepository;

    @Override
    public Page<ApprovalRecord> getApprovalRecords(Long userId, Pageable pageable) {
        userStatisticRepository.removeFlag(userId, RecordType.APPROVAL);
        return approvalRecordRepository.getApprovalRecords(userId, pageable);
    }

    @Override
    public void postApprovalRecord(Long fromUserId, ApprovalRecordReceiveDTO approvalRecordReceiveDTO) throws UserInfoException, PostException {
        ApprovalRecord newApprovalRecord = new ApprovalRecord();

        Comment newComment = commentRepository.getById(approvalRecordReceiveDTO.getCommentId());

        ApprovalStatus status = approvalRecordReceiveDTO.getStatus();
        if(status == ApprovalStatus.APPROVAL){
            newComment.addApproval();
            if(!fromUserId.equals(approvalRecordReceiveDTO.getToUserId())) {
                userStatisticRepository.setFlag(approvalRecordReceiveDTO.getToUserId(), RecordType.APPROVAL);
            }
        }
        else {
            newComment.addDisapproval();
        }
        newApprovalRecord.setApprovalStatus(status);

        newApprovalRecord.setComment(newComment);
        commentRepository.save(newComment);

        newApprovalRecord.setFromUser(userInfoRepository.getById(fromUserId));

        newApprovalRecord.setToUserId(approvalRecordReceiveDTO.getToUserId());

        newApprovalRecord.setTimestamp(new Timestamp(System.currentTimeMillis()));

        userStatisticRepository.getByUserId(approvalRecordReceiveDTO.getToUserId()).addApproval();
        approvalRecordRepository.save(newApprovalRecord);
    }

    @Override
    public void deleteApprovalRecord(Long fromUserId, ApprovalRecordReceiveDTO approvalRecordReceiveDTO) {
        Comment comment = commentRepository.getById(approvalRecordReceiveDTO.getCommentId());
        if (approvalRecordReceiveDTO.getStatus() == ApprovalStatus.APPROVAL) {
            comment.subApproval();
            userStatisticRepository.getByUserId(approvalRecordReceiveDTO.getToUserId()).subApproval();
        } else {
            comment.subDisapproval();
        }
        commentRepository.save(comment);
        approvalRecordRepository.deleteApprovalRecord(fromUserId, approvalRecordReceiveDTO.getCommentId());
    }

    @Override
    public ApprovalStatus checkIfHaveApproved(Long userId, Long commentId) throws UserInfoException, PostException {
        UserInfo userInfo = userInfoRepository.getById(userId);
        Comment comment = commentRepository.getById(commentId);
        return approvalRecordRepository.checkIfHaveApproved(userInfo, comment);
    }
}
