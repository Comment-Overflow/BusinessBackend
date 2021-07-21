package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.entity.ApprovalNotification;
import com.privateboat.forum.backend.repository.ApprovalNotificationRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.ApprovalNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class ApprovalNotificationServiceImpl implements ApprovalNotificationService {
    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    ApprovalNotificationRepository approvalNotificationRepository;

    @Override
    public Page<ApprovalNotification> getApprovalNotifications(Long userId, Pageable pageable) {
        return approvalNotificationRepository.getApprovalNotifications(userId, pageable);
    }

    @Override
    public void postApprovalNotification(Long fromUserId, Long toUserId, Long quoteId){
        ApprovalNotification newApprovalNotification = new ApprovalNotification();
        newApprovalNotification.setFromUser(userInfoRepository.findById(fromUserId));
        newApprovalNotification.setToUserId(toUserId);
        newApprovalNotification.setTimestamp(new Timestamp(System.currentTimeMillis()));
//        newApprovalNotification.setQuoteId(quoteId);

        approvalNotificationRepository.postApprovalNotification(newApprovalNotification);
    }
}
