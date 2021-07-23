package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.recordback.*;
import com.privateboat.forum.backend.dto.recordreceive.ApprovalRecordReceiveDTO;
import com.privateboat.forum.backend.dto.recordreceive.ReplyRecordReceiveDTO;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.service.ApprovalRecordService;
import com.privateboat.forum.backend.service.FollowRecordService;
import com.privateboat.forum.backend.service.ReplyRecordService;
import com.privateboat.forum.backend.service.StarRecordService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class RecordController {
    private final ModelMapper modelMapper;

    ApprovalRecordService approvalRecordService;
    StarRecordService starRecordService;
    ReplyRecordService replyRecordService;
    FollowRecordService followRecordService;

    @GetMapping(value = "/notifications/approvals")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<ApprovalRecordDTO>> getApprovalRecords(@RequestAttribute Long userId,
                                                               @RequestParam int page,
                                                               @RequestParam int pageSize) {
        Page<ApprovalRecordDTO> ret = approvalRecordService.getApprovalRecords(userId, PageRequest.of(page, pageSize)).map(
                approvalNotification -> modelMapper.map(approvalNotification, ApprovalRecordDTO.class)
        );
        return ResponseEntity.ok(ret.getContent());
    }

    @PostMapping(value = "/records/approvals")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> postApprovalRecord(@RequestAttribute Long fromUserId,
                                              ApprovalRecordReceiveDTO approvalRecordReceiveDTO) throws UserInfoException, PostException {
        approvalRecordService.postApprovalRecord(fromUserId, approvalRecordReceiveDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/records/approvals")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<ApprovalStatus> checkIfHaveApproved(@RequestAttribute Long userId,
                                                       @RequestParam Long commentId) throws UserInfoException, PostException {
        return ResponseEntity.ok(approvalRecordService.checkIfHaveApproved(userId, commentId));
    }

    @GetMapping(value = "/notifications/stars")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<StarRecordDTO>> getStarRecords(@RequestAttribute Long userId,
                                                       @RequestParam int page,
                                                       @RequestParam int pageSize){
        Page<StarRecordDTO> ret = starRecordService.getStarRecords(userId, PageRequest.of(page, pageSize)).map(
                starNotification -> modelMapper.map(starNotification, StarRecordDTO.class)
        );
        return ResponseEntity.ok(ret.getContent());
    }

    @PostMapping(value = "/records/stars")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> postStarRecord(@RequestAttribute Long userId,
                                          @RequestParam Long toUserId,
                                          @RequestParam Long postId) throws UserInfoException, PostException {
        starRecordService.postStarRecord(userId, toUserId, postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/records/stars")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<Boolean> checkIfHaveStarred(@RequestParam Long userId,
                                               @RequestParam Long postId){
        return ResponseEntity.ok(starRecordService.checkIfHaveStarred(userId, postId));
    }

    @GetMapping(value = "/notifications/replies")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<ReplyRecordDTO>> getReplyNotifications(@RequestAttribute Long userId,
                                                               @RequestParam int page,
                                                               @RequestParam int pageSize){
        Page<ReplyRecordDTO> ret = replyRecordService.getReplyRecords(userId, PageRequest.of(page, pageSize)).map(
                replyRecord -> modelMapper.map(replyRecord, ReplyRecordDTO.class)
        );
        return ResponseEntity.ok(ret.getContent());
    }

    @PostMapping(value = "/records/replies")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> postReplyRecord(@RequestAttribute Long userId,
                                                 ReplyRecordReceiveDTO replyRecordReceiveDTO) throws UserInfoException, PostException {
        replyRecordService.postReplyRecord(userId, replyRecordReceiveDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/records/followers")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<MyFollowRecordDTO>> getMyFollowRecords(@RequestAttribute Long userId,
                                                               @RequestParam int page,
                                                               @RequestParam int pageSize) throws UserInfoException {
        Page<MyFollowRecordDTO> ret = followRecordService.getFollowRecords(userId, PageRequest.of(page, pageSize)).map(
                followRecord -> modelMapper.map(followRecord, MyFollowRecordDTO.class)
        );
        return ResponseEntity.ok(ret.getContent());
    }

    @GetMapping(value = "/notifications/followers")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<FollowNotificationDTO>> getFollowNotifications(@RequestAttribute Long userId,
                                                                       @RequestParam int page,
                                                                       @RequestParam int pageSize) throws UserInfoException {
        Page<FollowNotificationDTO> ret = followRecordService.getFollowRecords(userId, PageRequest.of(page, pageSize)).map(
                followRecord -> modelMapper.map(followRecord, FollowNotificationDTO.class)
        );
        return ResponseEntity.ok(ret.getContent());
    }

    @PostMapping(value = "/records/followers")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<String> postFollowRecord(@RequestAttribute Long userId,
                                            @RequestParam Long toUserId) throws UserInfoException {
        followRecordService.postFollowRecord(userId, toUserId);
        return ResponseEntity.ok().build();
    }
}
