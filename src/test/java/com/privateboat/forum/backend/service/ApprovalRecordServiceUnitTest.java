package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.request.ApprovalRecordReceiveDTO;
import com.privateboat.forum.backend.entity.*;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import com.privateboat.forum.backend.enumerate.RecordType;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.ApprovalRecordRepository;
import com.privateboat.forum.backend.repository.CommentRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.repository.UserStatisticRepository;
import com.privateboat.forum.backend.serviceimpl.ApprovalRecordServiceImpl;
import com.privateboat.forum.backend.util.RedisUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.LinkedList;
import java.util.List;

import static com.privateboat.forum.backend.fakedata.Record.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;

class ApprovalRecordServiceUnitTest {

    private static Page<ApprovalRecord> APPROVAL_RECORD_PAGE;
    private static ApprovalRecordReceiveDTO VALID_APPROVAL_RECORD_RECEIVE_DTO;
    private static ApprovalRecordReceiveDTO VALID_DISAPPROVAL_RECORD_RECEIVE_DTO;
    private static ApprovalRecordReceiveDTO COMMENT_NOT_EXIST_APPROVAL_RECORD_RECEIVE_DTO;
    private static ApprovalRecordReceiveDTO TO_USER_NOT_EXIST_APPROVAL_RECORD_RECEIVE_DTO;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private ApprovalRecordRepository approvalRecordRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserStatisticRepository userStatisticRepository;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private ApprovalRecordServiceImpl approvalRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        List<ApprovalRecord> approvalRecordList = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            approvalRecordList.add(new ApprovalRecord());
        }
        APPROVAL_RECORD_PAGE = new PageImpl<>(approvalRecordList, PAGEABLE, approvalRecordList.size());

        UserInfo USER_INFO = new UserInfo();
        UserStatistic USER_STATISTIC = new UserStatistic(USER_INFO);
        USER_INFO.setUserStatistic(USER_STATISTIC);
        Comment COMMENT = new Comment(new Post(), USER_INFO, 1L, "content");

        VALID_APPROVAL_RECORD_RECEIVE_DTO = new ApprovalRecordReceiveDTO(VALID_COMMENT_ID, VALID_USER_ID, ApprovalStatus.APPROVAL);
        VALID_DISAPPROVAL_RECORD_RECEIVE_DTO = new ApprovalRecordReceiveDTO(VALID_COMMENT_ID, VALID_USER_ID, ApprovalStatus.DISAPPROVAL);
        COMMENT_NOT_EXIST_APPROVAL_RECORD_RECEIVE_DTO = new ApprovalRecordReceiveDTO(NOT_EXIST_COMMENT_ID, VALID_USER_ID, ApprovalStatus.APPROVAL);
        TO_USER_NOT_EXIST_APPROVAL_RECORD_RECEIVE_DTO = new ApprovalRecordReceiveDTO(VALID_COMMENT_ID, NOT_EXIST_USER_ID, ApprovalStatus.APPROVAL);


        //approvalRecordRepository get approval records
        Mockito.when(approvalRecordRepository.getApprovalRecords(VALID_USER_ID, PAGEABLE))
                .thenReturn(APPROVAL_RECORD_PAGE);

        //approvalRecordRepository NOT_EXIST_USER
        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
                .when(approvalRecordRepository)
                .getApprovalRecords(NOT_EXIST_USER_ID, PAGEABLE);

        //approvalRecordRepository NOT_EXIST_USER
        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
                .when(approvalRecordRepository)
                .deleteApprovalRecord(NOT_EXIST_USER_ID, VALID_COMMENT_ID);

        //approvalRecordRepository NOT_EXIST_COMMENT
        Mockito.doThrow(new PostException(PostException.PostExceptionType.COMMENT_NOT_EXIST))
                .when(approvalRecordRepository)
                .deleteApprovalRecord(VALID_USER_ID, NOT_EXIST_COMMENT_ID);

        //userStatisticRepository valid getByUserId
        Mockito.when(userStatisticRepository.getByUserId(VALID_USER_ID))
                .thenReturn(USER_STATISTIC);

        //userStatisticRepository remove flag NOT_EXIST_USER
        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
                .when(userStatisticRepository)
                .removeFlag(NOT_EXIST_USER_ID, RecordType.APPROVAL);

        //userStatisticRepository set flag NOT_EXIST_USER
        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
                .when(userStatisticRepository)
                .setFlag(NOT_EXIST_USER_ID, RecordType.APPROVAL);

        //commentRepository get valid comment
        Mockito.when(commentRepository.getById(VALID_COMMENT_ID))
                .thenReturn(COMMENT);

        //commentRepository NOT_EXIST_COMMENT
        Mockito.doThrow(new PostException(PostException.PostExceptionType.COMMENT_NOT_EXIST))
                .when(commentRepository)
                .getById(NOT_EXIST_COMMENT_ID);

        //userInfoRepository NOT_EXIST_USER
        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
                .when(userInfoRepository)
                .getById(NOT_EXIST_USER_ID);
    }
    @Test
    void testGetApprovalRecords() {
        //successfully getApprovalRecords
        try {
            Page<ApprovalRecord> approvalRecordPage = approvalRecordService.getApprovalRecords(VALID_USER_ID, PAGEABLE);
            Mockito.verify(userStatisticRepository).removeFlag(VALID_USER_ID, RecordType.APPROVAL);
            Mockito.verify(approvalRecordRepository).getApprovalRecords(VALID_USER_ID, PAGEABLE);
            assertSame(approvalRecordPage.getTotalElements(), APPROVAL_RECORD_PAGE.getTotalElements());
        } catch (Exception e) {
            assertNull(e);
        }

        //USER_NOT_EXIST getApprovalRecords
        UserInfoException userInfoException = Assertions.assertThrows(
                UserInfoException.class, () -> approvalRecordService.getApprovalRecords(NOT_EXIST_USER_ID, PAGEABLE)
        );
        assertSame(userInfoException.getType(), UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);
    }

    @Test
    void testPostApprovalRecord() {
        //post valid approval record
        try {
            approvalRecordService.postApprovalRecord(VALID_USER_ID, VALID_APPROVAL_RECORD_RECEIVE_DTO);
            Mockito.verify(commentRepository).getById(VALID_COMMENT_ID);
            Mockito.verify(commentRepository).save(any());
            Mockito.verify(userInfoRepository).getById(VALID_USER_ID);
        } catch (Exception e) {
            assertNull(e);
        }

        //post valid disapproval record
        try {
            approvalRecordService.postApprovalRecord(VALID_USER_ID, VALID_DISAPPROVAL_RECORD_RECEIVE_DTO);
            //this time it won't call setFlag
            Mockito.verify(userStatisticRepository, Mockito.never()).setFlag(any(), any());
        } catch (Exception e) {
            assertNull(e);
        }

        UserInfoException userInfoException;
        PostException postException;
        //FROM_USER_NOT_EXIST approval record
        userInfoException = Assertions.assertThrows(
                UserInfoException.class, () -> approvalRecordService.postApprovalRecord(NOT_EXIST_USER_ID, VALID_APPROVAL_RECORD_RECEIVE_DTO)
        );
        assertSame(userInfoException.getType(), UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);

        //TO_USER_NOT_EXIST approval record
        userInfoException = Assertions.assertThrows(
                UserInfoException.class, () -> approvalRecordService.postApprovalRecord(VALID_USER_ID, TO_USER_NOT_EXIST_APPROVAL_RECORD_RECEIVE_DTO)
        );
        assertSame(userInfoException.getType(), UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);

        //COMMENT_NOT_EXIST approval record
        postException = Assertions.assertThrows(
                PostException.class, () -> approvalRecordService.postApprovalRecord(VALID_USER_ID, COMMENT_NOT_EXIST_APPROVAL_RECORD_RECEIVE_DTO)
        );
        assertSame(postException.getType(), PostException.PostExceptionType.COMMENT_NOT_EXIST);

    }

    @Test
    void testDeleteApprovalRecord() {
        //delete valid approval record
        try {
            approvalRecordService.deleteApprovalRecord(VALID_USER_ID, VALID_APPROVAL_RECORD_RECEIVE_DTO);
            Mockito.verify(commentRepository).getById(VALID_COMMENT_ID);
            Mockito.verify(commentRepository).save(any());
            Mockito.verify(approvalRecordRepository).deleteApprovalRecord(VALID_USER_ID, VALID_COMMENT_ID);
        } catch (Exception e) {
            assertNull(e);
        }

        //delete valid disapproval record
        try {
            approvalRecordService.deleteApprovalRecord(VALID_USER_ID, VALID_DISAPPROVAL_RECORD_RECEIVE_DTO);
        } catch (Exception e) {
            assertNull(e);
        }

        //delete NOT_EXIST_USER
        UserInfoException userInfoException = Assertions.assertThrows(
                UserInfoException.class, () -> approvalRecordService.deleteApprovalRecord(NOT_EXIST_USER_ID, VALID_APPROVAL_RECORD_RECEIVE_DTO)
        );
        assertSame(userInfoException.getType(), UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);

        //delete NOT_EXIST_COMMENT
        PostException postException = Assertions.assertThrows(
                PostException.class, () -> approvalRecordService.deleteApprovalRecord(VALID_USER_ID, COMMENT_NOT_EXIST_APPROVAL_RECORD_RECEIVE_DTO)
        );
        assertSame(postException.getType(), PostException.PostExceptionType.COMMENT_NOT_EXIST);
    }
}