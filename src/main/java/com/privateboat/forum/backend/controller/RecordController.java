package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.recordback.ApprovalRecordDTO;
import com.privateboat.forum.backend.dto.recordback.FollowNotificationDTO;
import com.privateboat.forum.backend.dto.recordback.MyFollowRecordDTO;
import com.privateboat.forum.backend.dto.recordback.StarRecordDTO;
import com.privateboat.forum.backend.dto.recordreceive.ApprovalRecordReceiveDTO;
import com.privateboat.forum.backend.entity.ApprovalRecord;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.service.ApprovalRecordService;
import com.privateboat.forum.backend.service.FollowRecordService;
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
//    ReplyNotificationService replyRecordService;
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

    @GetMapping(value = "/notifications/stars")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<Page<StarRecordDTO>> getStarRecords(@RequestAttribute Long userId,
                                                       @RequestParam int page,
                                                       @RequestParam int pageSize){
        Page<StarRecordDTO> ret = starRecordService.getStarRecords(userId, PageRequest.of(page, pageSize)).map(
                starNotification -> modelMapper.map(starNotification, StarRecordDTO.class)
        );
        return ResponseEntity.ok(ret);
    }

    @PostMapping(value = "/records/stars")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> postStarRecord(@RequestAttribute Long userId,
                                          @RequestParam Long toUserId,
                                          @RequestParam Long postId) throws UserInfoException, PostException {
        starRecordService.postStarRecord(userId, toUserId, postId);
        return ResponseEntity.ok().build();
    }

//    @GetMapping(value = "/records/replies")
//    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
//    ResponseEntity<Page<ReplyNotificationDTO>> getReplyNotifications(@RequestAttribute Long userId,
//                                                                     @RequestParam int page,
//                                                                     @RequestParam int pageSize){
//        Page<ReplyNotificationDTO> ret = replyRecordService.getReplyNotifications(userId, PageRequest.of(page, pageSize)).map(
//                replyNotification -> modelMapper.map(replyNotification, ReplyNotificationDTO.class)
//        );
//        return ResponseEntity.ok(ret);
//    }

//    @PostMapping(value = "/records/replies")
//    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
//    ResponseEntity<String> postReplyNotification(@RequestAttribute Long userId,
//                                                 @RequestParam Long toUserId,
//                                                 @RequestParam Long quoteId){
//        replyRecordService.postReplyNotification(userId, toUserId, quoteId);
//        return ResponseEntity.ok().build();
//    }

    @GetMapping(value = "/records/followers")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<Page<MyFollowRecordDTO>> getMyFollowRecord(@RequestAttribute Long userId,
                                                            @RequestParam int page,
                                                            @RequestParam int pageSize) throws UserInfoException {
        Page<MyFollowRecordDTO> ret = followRecordService.getFollowRecords(userId, PageRequest.of(page, pageSize)).map(
                followRecord -> modelMapper.map(followRecord, MyFollowRecordDTO.class)
        );
        return ResponseEntity.ok(ret);
    }

    @GetMapping(value = "/notifications/records")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<Page<FollowNotificationDTO>> getFollowNotifications(@RequestAttribute Long userId,
                                                                       @RequestParam int page,
                                                                       @RequestParam int pageSize) throws UserInfoException {
        Page<FollowNotificationDTO> ret = followRecordService.getFollowRecords(userId, PageRequest.of(page, pageSize)).map(
                followRecord -> modelMapper.map(followRecord, FollowNotificationDTO.class)
        );
        return ResponseEntity.ok(ret);
    }

    @PostMapping(value = "/records/followers")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<String> postFollowRecord(@RequestAttribute Long userId,
                                            @RequestParam Long toUserId) throws UserInfoException {
        followRecordService.postFollowRecord(userId, toUserId);
        return ResponseEntity.ok().build();
    }
}
