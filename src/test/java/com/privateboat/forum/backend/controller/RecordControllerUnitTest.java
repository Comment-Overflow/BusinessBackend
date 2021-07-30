package com.privateboat.forum.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateboat.forum.backend.dto.request.ApprovalRecordReceiveDTO;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.fakedata.Record;
import com.privateboat.forum.backend.interceptor.JWTInterceptor;
import com.privateboat.forum.backend.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.privateboat.forum.backend.fakedata.Record.approvalRecordReceiveDTOUserNotExist;
import static com.privateboat.forum.backend.fakedata.Record.newlyRecordDTO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecordController.class)
class RecordControllerUnitTest {
    static final private String EMAIL = "gungnir_guo@sjtu.edu.cn";
    static final private String PASSWORD = "guozhdiong12";
    static final private String WRONG_PASSWORD = "abc";
    static final private String CORRECT_EMAIL_CODE = "123456";
    static final private String WRONG_EMAIL_CODE = "654321";
    static final private String EXPIRED_EMAIL_CODE = "123123";
    static final private String FAKE_TOKEN = "";
    static final private Long USER_ID = 1L;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApprovalRecordService approvalRecordService;

    @MockBean
    private StarRecordService starRecordService;

    @MockBean
    private ReplyRecordService replyRecordService;

    @MockBean
    private FollowRecordService followRecordService;

    @MockBean
    private UserStatisticService userStatisticService;

    @MockBean
    private JWTInterceptor jwtInterceptor;

    @BeforeEach
    void setUp() {
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void testGetNewlyRecords() {

        given(userStatisticService.getNewlyRecords(USER_ID)).willReturn(newlyRecordDTO);
    }

    @Test
    void testGetApprovalRecords() {

    }

    @Test
    void testPostApprovalRecord() throws Exception {
        ApprovalRecordReceiveDTO approvalRecordReceiveDTO = Record.approvalRecordReceiveDTO;
        mvc.perform(post("/records/approvals")
                .requestAttr("userId", Record.USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(approvalRecordReceiveDTO)))
                .andExpect(status().isCreated());

        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
                .when(approvalRecordService)
                .postApprovalRecord(Record.USER_ID, approvalRecordReceiveDTO);

        mvc.perform(post("/records/approvals")
                .requestAttr("userId", Record.USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(approvalRecordReceiveDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteApprovalRecord() {
    }

    @Test
    void checkIfHaveApproved() {
    }

    @Test
    void getStarRecords() {
    }

    @Test
    void testPostStarRecord() {

    }

    @Test
    void deleteStarRecord() {
    }

    @Test
    void checkIfHaveStarred() {
    }

    @Test
    void getReplyNotifications() {
    }

    @Test
    void postReplyRecord() {
    }

    @Test
    void getMyFollowingRecords() {
    }

    @Test
    void getMyFollowedRecords() {
    }

    @Test
    void getFollowNotifications() {
    }

    @Test
    void postFollowRecord() {
    }

    @Test
    void deleteFollowRecord() {
    }
}