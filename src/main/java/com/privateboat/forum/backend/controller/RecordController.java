package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.request.ApprovalRecordReceiveDTO;
import com.privateboat.forum.backend.dto.request.ReplyRecordReceiveDTO;
import com.privateboat.forum.backend.dto.response.*;
import com.privateboat.forum.backend.entity.UserStatistic;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.service.*;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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
    UserStatisticService userStatisticService;


    @GetMapping(value = "/notifications/new_records")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<NewlyRecordDTO> getNewlyRecords(@RequestAttribute Long userId) {
        try {
            UserStatistic userStatistic = userStatisticService.getNewlyRecords(userId);
            return ResponseEntity.ok(modelMapper.map(userStatistic, NewlyRecordDTO.class));
        } catch (RuntimeException e) {
            System.out.println(userId.toString() + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/notifications/approvals")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<ApprovalRecordDTO>> getApprovalRecords(@RequestAttribute Long userId,
                                                               @RequestParam int page,
                                                               @RequestParam int pageSize) {
        try {
            Page<ApprovalRecordDTO> ret = approvalRecordService.getApprovalRecords(userId, PageRequest.of(page, pageSize)).map(
                    approvalNotification -> modelMapper.map(approvalNotification, ApprovalRecordDTO.class)
            );
            return ResponseEntity.ok(ret.getContent());
        } catch (RuntimeException e) {
            System.out.println(userId.toString() + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/records/approvals")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<String> postApprovalRecord(@RequestParam Long fromUserId,
                                              ApprovalRecordReceiveDTO approvalRecordReceiveDTO) throws UserInfoException, PostException {
        try{
            approvalRecordService.postApprovalRecord(fromUserId, approvalRecordReceiveDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e){
            System.out.println(fromUserId.toString() + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/records/approvals")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<ApprovalStatus> checkIfHaveApproved(@RequestAttribute Long userId,
                                                       @RequestParam Long commentId) throws UserInfoException, PostException {
        try {
            ApprovalStatus status = approvalRecordService.checkIfHaveApproved(userId, commentId);
            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            System.out.println(userId.toString() + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/notifications/stars")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<StarRecordDTO>> getStarRecords(@RequestAttribute Long userId,
                                                       @RequestParam int page,
                                                       @RequestParam int pageSize) {
        try {
            Page<StarRecordDTO> ret = starRecordService.getStarRecords(userId, PageRequest.of(page, pageSize)).map(
                    starNotification -> modelMapper.map(starNotification, StarRecordDTO.class)
            );
            return ResponseEntity.ok(ret.getContent());
        } catch (RuntimeException e) {
            System.out.println(userId.toString() + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/records/stars")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> postStarRecord(@RequestAttribute Long userId,
                                          @RequestParam Long toUserId,
                                          @RequestParam Long postId) throws UserInfoException, PostException {
        try{
            starRecordService.postStarRecord(userId, toUserId, postId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e){
            System.out.println(userId.toString() + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/records/stars")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<Boolean> checkIfHaveStarred(@RequestParam Long userId,
                                               @RequestParam Long postId) {
        try {
            return ResponseEntity.ok(starRecordService.checkIfHaveStarred(userId, postId));
        } catch (RuntimeException e) {
            System.out.println(userId.toString() + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/notifications/replies")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<ReplyRecordDTO>> getReplyNotifications(@RequestAttribute Long userId,
                                                               @RequestParam int page,
                                                               @RequestParam int pageSize) {
        try {
            Page<ReplyRecordDTO> ret = replyRecordService.getReplyRecords(userId, PageRequest.of(page, pageSize)).map(
                    replyRecord -> modelMapper.map(replyRecord, ReplyRecordDTO.class)
            );
            return ResponseEntity.ok(ret.getContent());
        } catch (RuntimeException e) {
            System.out.println(userId.toString() + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/records/replies")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> postReplyRecord(@RequestAttribute Long userId,
                                                 ReplyRecordReceiveDTO replyRecordReceiveDTO) throws UserInfoException, PostException {
        try {
            replyRecordService.postReplyRecord(userId, replyRecordReceiveDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            System.out.println(userId.toString() + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/records/followers")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<MyFollowRecordDTO>> getMyFollowRecords(@RequestAttribute Long userId,
                                                               @RequestParam int page,
                                                               @RequestParam int pageSize) throws UserInfoException {
        try{
            Page<MyFollowRecordDTO> ret = followRecordService.getFollowRecords(userId, PageRequest.of(page, pageSize)).map(
                    followRecord -> modelMapper.map(followRecord, MyFollowRecordDTO.class)
            );
            return ResponseEntity.ok(ret.getContent());
        } catch (RuntimeException e) {
            System.out.println(userId.toString() + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/notifications/followers")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<FollowNotificationDTO>> getFollowNotifications(@RequestAttribute Long userId,
                                                                       @RequestParam int page,
                                                                       @RequestParam int pageSize) throws UserInfoException {
        try {
            Page<FollowNotificationDTO> ret = followRecordService.getFollowRecords(userId, PageRequest.of(page, pageSize)).map(
                    followRecord -> modelMapper.map(followRecord, FollowNotificationDTO.class)
            );
            return ResponseEntity.ok(ret.getContent());
        } catch (RuntimeException e) {
            System.out.println(userId.toString() + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping(value = "/records/followers")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<String> postFollowRecord(@RequestAttribute Long userId,
                                            @RequestParam Long toUserId) throws UserInfoException {
        try {
            followRecordService.postFollowRecord(userId, toUserId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e){
            System.out.println(userId.toString() + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
