package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.ApprovalNotificationDTO;
import com.privateboat.forum.backend.service.ApprovalNotificationService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class NotificationController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    ApprovalNotificationService approvalNotificationService;

    @GetMapping(value = "/notifications/approvals")
//    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<Page<ApprovalNotificationDTO>> getApprovalNotifications(@RequestParam Long userId,
                                                                           @RequestParam int page,
                                                                           @RequestParam int pageSize) {
        Page<ApprovalNotificationDTO> ret = approvalNotificationService.getApprovalNotifications(userId, PageRequest.of(page, pageSize)).map(
                approvalNotification -> modelMapper.map(approvalNotification, ApprovalNotificationDTO.class)
        );
        return ResponseEntity.ok(ret);
    }

    @PostMapping(value = "/notifications/approvals")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<String> postApprovalNotification(@RequestParam Long fromUserId,
                                                    @RequestParam Long toUserId,
                                                    @RequestParam Long quoteId){
        System.out.println(fromUserId);
        System.out.println(toUserId);
        approvalNotificationService.postApprovalNotification(fromUserId, toUserId, quoteId);
        return ResponseEntity.ok().build();
    }
}
