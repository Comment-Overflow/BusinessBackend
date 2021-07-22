package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.ApprovalRecordDAO;
import com.privateboat.forum.backend.entity.ApprovalRecord;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import com.privateboat.forum.backend.repository.ApprovalRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ApprovalRecordRepositoryImpl implements ApprovalRecordRepository {
    private final ApprovalRecordDAO approvalRecordDAO;

    @Override
    public Page<ApprovalRecord> getApprovalRecords(Long toUserId, Pageable pageable){
        return approvalRecordDAO.findByToUserIdAndStatusIsNot(toUserId, ApprovalStatus.APPROVAL, pageable);
    }

    @Override
    public void postApprovalRecord(ApprovalRecord newApprovalRecord){
        approvalRecordDAO.saveAndFlush(newApprovalRecord);
    }
}
