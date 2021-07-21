package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.ApprovalNotificationDTO;
import com.privateboat.forum.backend.dto.StarNotificationDTO;
import com.privateboat.forum.backend.service.ApprovalNotificationService;
import com.privateboat.forum.backend.service.StarNotificationService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class NotificationController {

    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    ApprovalNotificationService approvalNotificationService;
    @Autowired
    StarNotificationService starNotificationService;
    @Autowired
    ReplyNotificationService replyNotificationService;
    @Autowired
    FollowNotificationService followNotificationService;

    @GetMapping(value = "/notifications/approvals")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<Page<ApprovalNotificationDTO>> getApprovalNotifications(@RequestAttribute Long userId,
                                                                           @RequestParam int page,
                                                                           @RequestParam int pageSize) {
        Page<ApprovalNotificationDTO> ret = approvalNotificationService.getApprovalNotifications(userId, PageRequest.of(page, pageSize)).map(
                approvalNotification -> modelMapper.map(approvalNotification, ApprovalNotificationDTO.class)
        );
        return ResponseEntity.ok(ret);
    }

    @PostMapping(value = "/notifications/approvals")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> postApprovalNotification(@RequestAttribute Long userId,
                                                    @RequestParam Long toUserId,
                                                    @RequestParam Long quoteId){
        approvalNotificationService.postApprovalNotification(userId, toUserId, quoteId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/notifications/stars")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<Page<StarNotificationDTO>> getStarNotifications(@RequestAttribute Long userId,
                                                                   @RequestParam int page,
                                                                   @RequestParam int pageSize){
        Page<StarNotificationDTO> ret = starNotificationService.getStarNotifications(userId, PageRequest.of(page, pageSize)).map(
                starNotification -> modelMapper.map(starNotification, StarNotificationDTO.class)
        );
        return ResponseEntity.ok(ret);
    }

    @PostMapping(value = "/notifications/stars")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> postStarNotification(@RequestAttribute Long userId,
                                                @RequestParam Long toUserId,
                                                @RequestParam Long postId){
        approvalNotificationService.postApprovalNotification(userId, toUserId, postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/notifications/replies")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<Page<ReplyNotificationDTO>> getReplyNotifications(@RequestAttribute Long userId,
                                                                     @RequestParam int page,
                                                                     @RequestParam int pageSize){
        Page<ReplyNotificationDTO> ret = replyNotificationService.getReplyNotifications(userId, PageRequest.of(page, pageSize)).map(
                replyNotification -> modelMapper.map(replyNotification, ReplyNotificationDTO.class)
        );
        return ResponseEntity.ok(ret);
    }

    @PostMapping(value = "/notifications/replies")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> postReplyNotification(@RequestAttribute Long userId,
                                                 @RequestParam Long toUserId,
                                                 @RequestParam Long quoteId){
        replyNotificationService.postReplyNotification(userId, toUserId, quoteId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/notifications/followers")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<Page<followNotificationDTO>> getFollowNotification(@RequestAttribute Long userId,
                                                                      @RequestParam int page,
                                                                      @RequestParam int pageSize){
        Page<followNotificationDTO> ret = followNotificationService.getFollowNotifications(userId, PageRequest.of(page, pageSize)).map(
                followNotification -> modelMapper.map(followNotification, FollowNotificationDTO.class)
        );
        return ResponseEntity.ok(ret);
    }

    @PostMapping(value = "/notifications/followers")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<String> postReplyNotification(@RequestAttribute Long userId,
                                                 @RequestParam Long toUserId){
        replyNotificationService.postReplyNotification(userId, toUserId);
        return ResponseEntity.ok().build();
    }
}
