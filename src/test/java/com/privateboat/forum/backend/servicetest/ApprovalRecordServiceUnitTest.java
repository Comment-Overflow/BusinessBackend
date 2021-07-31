//package com.privateboat.forum.backend.servicetest;
//
//import com.privateboat.forum.backend.entity.*;
//import com.privateboat.forum.backend.enumerate.Gender;
//import com.privateboat.forum.backend.enumerate.RecordType;
//import com.privateboat.forum.backend.exception.UserInfoException;
//import com.privateboat.forum.backend.repository.ApprovalRecordRepository;
//import com.privateboat.forum.backend.repository.CommentRepository;
//import com.privateboat.forum.backend.repository.UserInfoRepository;
//import com.privateboat.forum.backend.repository.UserStatisticRepository;
//import com.privateboat.forum.backend.service.ApprovalRecordService;
//import com.privateboat.forum.backend.serviceimpl.ApprovalRecordServiceImpl;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//
//import java.util.LinkedList;
//import java.util.List;
//
//import static com.privateboat.forum.backend.fakedata.Record.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//class ApprovalRecordServiceUnitTest {
//
//    private static Page<ApprovalRecord> APPROVAL_RECORD_PAGE;
//    private static Comment COMMENT;
//    private static Post POST;
//    private static UserInfo USER_INFO;
//
//    @Mock
//    private UserInfoRepository userInfoRepository;
//
//    @Mock
//    private ApprovalRecordRepository approvalRecordRepository;
//
//    @Mock
//    private CommentRepository commentRepository;
//
//    @Mock
//    private UserStatisticRepository userStatisticRepository;
//
//    @InjectMocks
//    private ApprovalRecordServiceImpl approvalRecordService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        List<ApprovalRecord> approvalRecordList = new LinkedList<>();
//        for (int i = 0; i < 10; i++) {
//            approvalRecordList.add(new ApprovalRecord());
//        }
//        APPROVAL_RECORD_PAGE = new PageImpl<>(approvalRecordList, PAGEABLE, approvalRecordList.size());
//
//        USER_INFO = new UserInfo(VALID_USER_ID, "WXP", Gender.MALE, null, null, new UserAuth(), );
//        COMMENT = new Comment(1L, new Post(), );
//        //get approvalRecord
//        Mockito.when(approvalRecordRepository.getApprovalRecords(VALID_USER_ID, PAGEABLE))
//                .thenReturn(APPROVAL_RECORD_PAGE);
//
//        //get approvalRecord NOT_EXIST_USER
//        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
//                .when(approvalRecordRepository)
//                .getApprovalRecords(NOT_EXIST_USER_ID, PAGEABLE);
//
//        //userStatisticRepository remove flag NOT_EXIST_USER
//        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
//                .when(userStatisticRepository)
//                .removeFlag(NOT_EXIST_USER_ID, RecordType.APPROVAL);
//        //userStatisticRepository set flag NOT_EXIST_USER
//        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
//                .when(userStatisticRepository)
//                .setFlag(NOT_EXIST_USER_ID, RecordType.APPROVAL);
//
//        Mockito.when(commentRepository.getById(VALID_COMMENT_ID))
//                .thenReturn(new Comment());
//
//    }
//    @Test
//    void getApprovalRecords() {
//        //successfully getApprovalRecords
//        try {
//            Page<ApprovalRecord> approvalRecordPage = approvalRecordService.getApprovalRecords(VALID_USER_ID, PAGEABLE);
//            Mockito.verify(userStatisticRepository).removeFlag(VALID_USER_ID, RecordType.APPROVAL);
//            Mockito.verify(approvalRecordRepository).getApprovalRecords(VALID_USER_ID, PAGEABLE);
//            assertSame(approvalRecordPage.getTotalElements(), APPROVAL_RECORD_PAGE.getTotalElements());
//        } catch (Exception e) {
//            assertNull(e);
//        }
//
//        //USER_NOT_EXIST getApprovalRecords
//        UserInfoException userInfoException = Assertions.assertThrows(
//                UserInfoException.class, () -> approvalRecordService.getApprovalRecords(NOT_EXIST_USER_ID, PAGEABLE)
//        );
//        assertSame(userInfoException.getType(), UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);
//    }
//
//    @Test
//    void postApprovalRecord() {
//
//    }
//
//    @Test
//    void deleteApprovalRecord() {
//
//    }
//
//    @Test
//    void checkIfHaveApproved() {
//
//    }
//}