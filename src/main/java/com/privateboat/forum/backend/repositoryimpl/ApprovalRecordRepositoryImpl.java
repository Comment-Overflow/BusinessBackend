package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.ApprovalRecordDAO;
import com.privateboat.forum.backend.entity.ApprovalRecord;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import com.privateboat.forum.backend.repository.ApprovalRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class ApprovalRecordRepositoryImpl implements ApprovalRecordRepository {
    private final ApprovalRecordDAO approvalRecordDAO;

    @Override
    public Page<ApprovalRecord> getApprovalRecords(Long toUserId, Pageable pageable){
        return approvalRecordDAO.findByToUserIdAndApprovalStatus(toUserId, ApprovalStatus.APPROVAL, pageable);
    }

    @Override
    public void deleteApprovalRecord(Long userId, Long commentId) {
        approvalRecordDAO.deleteByToUserIdAndCommentId(userId, commentId);
    }

    @Override
    public void save(ApprovalRecord newApprovalRecord){
        approvalRecordDAO.saveAndFlush(newApprovalRecord);
    }

    @Override
    public ApprovalStatus checkIfHaveApproved(UserInfo userInfo, Comment comment){
        Optional<ApprovalRecord> approvalRecord = approvalRecordDAO.findByFromUserAndComment(userInfo, comment);
        if(approvalRecord.isPresent()) {
            return approvalRecord.get().getApprovalStatus();
        }
        else return ApprovalStatus.NONE;
    }
}
