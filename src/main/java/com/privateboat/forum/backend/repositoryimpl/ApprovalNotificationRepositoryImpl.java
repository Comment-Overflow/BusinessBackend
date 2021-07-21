package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.ApprovalNotificationDAO;
import com.privateboat.forum.backend.entity.ApprovalNotification;
import com.privateboat.forum.backend.repository.ApprovalNotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ApprovalNotificationRepositoryImpl implements ApprovalNotificationRepository {
    private final ApprovalNotificationDAO approvalNotificationDAO;

    @Override
    public Page<ApprovalNotification> getApprovalNotifications(Long userId, Pageable pageable){
        return approvalNotificationDAO.findByToUserId(userId, pageable);
    }

    @Override
    public void postApprovalNotification(ApprovalNotification newApprovalNotification){
        approvalNotificationDAO.saveAndFlush(newApprovalNotification);
    }
}
