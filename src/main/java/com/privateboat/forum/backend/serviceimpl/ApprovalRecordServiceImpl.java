package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.request.ApprovalRecordReceiveDTO;
import com.privateboat.forum.backend.entity.*;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import com.privateboat.forum.backend.enumerate.RecordType;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.rabbitmq.MQSender;
import com.privateboat.forum.backend.repository.*;
import com.privateboat.forum.backend.service.ApprovalRecordService;
import com.privateboat.forum.backend.util.RedisUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class ApprovalRecordServiceImpl implements ApprovalRecordService {
    private final RedisUtil redisUtil;
    private final UserInfoRepository userInfoRepository;
    private final ApprovalRecordRepository approvalRecordRepository;
    private final CommentRepository commentRepository;
    private final UserStatisticRepository userStatisticRepository;
    private final MQSender mqSender;

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
            Post post = newComment.getPost();
            newComment.addApproval();
            post.incrementApproval();
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

        // userStatisticRepository.getByUserId(approvalRecordReceiveDTO.getToUserId()).addApproval();
        // userStatisticRepository.addApprovalCount(approvalRecordReceiveDTO.getToUserId());
        userStatisticRepository.updateApprovalCount(approvalRecordReceiveDTO.getToUserId());
        approvalRecordRepository.save(newApprovalRecord);
        redisUtil.addApprovalCount();
        updateCache(newComment.getPost().getId(), newComment.getFloor(), 8);
    }

    @Override
    public void deleteApprovalRecord(Long fromUserId, ApprovalRecordReceiveDTO approvalRecordReceiveDTO) {
        Comment comment = commentRepository.getById(approvalRecordReceiveDTO.getCommentId());
        if (approvalRecordReceiveDTO.getStatus() == ApprovalStatus.APPROVAL) {
            Post post = comment.getPost();
            post.decrementApproval();
            comment.subApproval();
            // userStatisticRepository.getByUserId(approvalRecordReceiveDTO.getToUserId()).subApproval();
            // userStatisticRepository.subApprovalCount(approvalRecordReceiveDTO.getToUserId());
            userStatisticRepository.updateApprovalCount(approvalRecordReceiveDTO.getToUserId());
        } else {
            comment.subDisapproval();
        }
        commentRepository.save(comment);
        approvalRecordRepository.deleteApprovalRecord(fromUserId, approvalRecordReceiveDTO.getCommentId());
        updateCache(comment.getPost().getId(), comment.getFloor(), 8);
    }

    @Override
    public ApprovalStatus checkIfHaveApproved(Long userId, Long commentId) throws UserInfoException, PostException {
        UserInfo userInfo = userInfoRepository.getById(userId);
        Comment comment = commentRepository.getById(commentId);
        return approvalRecordRepository.checkIfHaveApproved(userInfo, comment);
    }

    private void updateCache(Long postId, Integer commentFloor, Integer pageSize) {
        int pageNum = commentFloor / pageSize;
        Pageable pageable = PageRequest.of(pageNum, pageSize,
                Sort.by(Sort.Direction.ASC, "floor"));
        // mqSender.sendCacheUpdateMessage(postId, pageNum, pageSize);
        commentRepository.updateCommentCache(postId, pageable);
    }
}
