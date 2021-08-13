package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.request.ApprovalRecordReceiveDTO;
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
    ResponseEntity<UserStatistic.NewlyRecord> getNewlyRecords(@RequestAttribute Long userId) {
        try {
            return ResponseEntity.ok(userStatisticService.getNewlyRecords(userId));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/notifications/approvals")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<PageDTO<ApprovalRecordDTO>> getApprovalRecords(@RequestAttribute Long userId,
                                                                  @RequestParam int page,
                                                                  @RequestParam int pageSize) {
        try {
            Page<ApprovalRecordDTO> ret = approvalRecordService.getApprovalRecords(userId, PageRequest.of(page, pageSize)).map(
                    approvalNotification -> modelMapper.map(approvalNotification, ApprovalRecordDTO.class)
            );
            return ResponseEntity.ok(new PageDTO<>(ret));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping(value = "/records/approvals")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> postApprovalRecord(@RequestAttribute Long userId,
                                              @RequestBody ApprovalRecordReceiveDTO approvalRecordReceiveDTO) throws UserInfoException, PostException {
        try{
            approvalRecordService.postApprovalRecord(userId, approvalRecordReceiveDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping(value = "/records/approvals")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> deleteApprovalRecord(@RequestAttribute Long userId,
                                                @RequestBody ApprovalRecordReceiveDTO approvalRecordReceiveDTO) {
        try {
            approvalRecordService.deleteApprovalRecord(userId, approvalRecordReceiveDTO);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/notifications/stars")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<PageDTO<StarRecordDTO>> getStarRecords(@RequestAttribute Long userId,
                                                          @RequestParam int page,
                                                          @RequestParam int pageSize) {
        try {
            Page<StarRecordDTO> ret = starRecordService.getStarRecords(userId, PageRequest.of(page, pageSize)).map(
                    starNotification -> modelMapper.map(starNotification, StarRecordDTO.class)
            );
            return ResponseEntity.ok(new PageDTO<>(ret));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping(value = "/records/stars")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> postStarRecord(@RequestAttribute Long userId,
                                          @RequestParam Long toUserId,
                                          @RequestParam Long postId) {
        try {
            starRecordService.postStarRecord(userId, toUserId, postId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping(value = "/records/stars")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> deleteStarRecord(@RequestAttribute Long userId,
                                            @RequestParam Long postId) {
        try {
            starRecordService.deleteStarRecord(userId, postId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/notifications/replies")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<ReplyRecordDTO>> getReplyNotifications(@RequestAttribute Long userId,
                                                               @RequestParam int page,
                                                               @RequestParam int pageSize) {
        try {
            return ResponseEntity.ok(replyRecordService.getReplyRecords(userId, PageRequest.of(page, pageSize)));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/records/following")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<PageDTO<UserCardInfoDTO>> getMyFollowingRecords(@RequestAttribute Long userId,
                                                                     @RequestParam int page,
                                                                     @RequestParam int pageSize) {
        try{
            Page<UserCardInfoDTO> ret = followRecordService.getFollowingRecords(userId, PageRequest.of(page, pageSize));
            return ResponseEntity.ok(new PageDTO<>(ret));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/records/followed")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<PageDTO<UserCardInfoDTO>> getMyFollowedRecords(@RequestAttribute Long userId,
                                                               @RequestParam int page,
                                                               @RequestParam int pageSize) {
        try {
            Page<UserCardInfoDTO> ret = followRecordService.getFollowedRecords(userId, PageRequest.of(page, pageSize));
            return ResponseEntity.ok(new PageDTO<>(ret));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/notifications/followers")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<PageDTO<FollowNotificationDTO>> getFollowNotifications(@RequestAttribute Long userId,
                                                                       @RequestParam int page,
                                                                       @RequestParam int pageSize) {
        try {
            Page<FollowNotificationDTO> ret = followRecordService.getFollowingNotifications(userId, PageRequest.of(page, pageSize)).map(
                    followRecord -> modelMapper.map(followRecord, FollowNotificationDTO.class)
            );
            return ResponseEntity.ok(new PageDTO<>(ret));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping(value = "/records/followers")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> postFollowRecord(@RequestAttribute Long userId,
                                            @RequestParam Long toUserId) {
        try {
            followRecordService.postFollowRecord(userId, toUserId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping(value = "/records/followers")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> deleteFollowRecord(@RequestAttribute Long userId,
                                              @RequestParam Long toUserId){
        try {
            followRecordService.deleteFollowRecord(userId, toUserId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
