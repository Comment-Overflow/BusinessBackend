package com.privateboat.forum.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateboat.forum.backend.dto.response.ApprovalRecordDTO;
import com.privateboat.forum.backend.entity.ApprovalRecord;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.interceptor.JWTInterceptor;
import com.privateboat.forum.backend.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedList;
import java.util.List;

import static com.privateboat.forum.backend.fakedata.Record.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecordController.class)
class RecordControllerUnitTest {
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
    private ModelMapper modelMapper;

    @MockBean
    private UserStatisticService userStatisticService;

    @MockBean
    private JWTInterceptor jwtInterceptor;

    @BeforeEach
    void setUp() {
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void testGetNewlyRecords() throws Exception {
        //get valid NewlyRecord
        given(userStatisticService.getNewlyRecords(VALID_USER_ID)).willReturn(USER_STATISTIC_NEW_RECORD);
        mvc.perform(get("/notifications/new_records")
                .requestAttr("userId", VALID_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(USER_STATISTIC_NEW_RECORD)));

        //get NOT_EXIST_USER NewlyRecord
        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
                .when(userStatisticService)
                .getNewlyRecords(NOT_EXIST_USER_ID);

        mvc.perform(get("/notifications/new_records")
                .requestAttr("userId", NOT_EXIST_USER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetApprovalRecords() throws Exception {
        //construct Approval data
        List<ApprovalRecord> approvalRecordList = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            approvalRecordList.add(new ApprovalRecord());
        }
        Page<ApprovalRecord> approvalRecordPage = new PageImpl<>(approvalRecordList, PAGEABLE, approvalRecordList.size());
        List<ApprovalRecordDTO> approvalRecordDTOList = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            approvalRecordDTOList.add(new ApprovalRecordDTO());
        }
        Page<ApprovalRecordDTO> approvalRecordDTOPage = new PageImpl<>(approvalRecordDTOList, PAGEABLE, approvalRecordList.size());
        //get valid approval records
        given(approvalRecordService.getApprovalRecords(VALID_USER_ID, PAGEABLE)).willReturn(approvalRecordPage);
        mvc.perform(get("/notifications/approvals")
                .requestAttr("userId", VALID_USER_ID)
                .param("page", String.valueOf(PAGE_OFFSET))
                .param("pageSize", String.valueOf(PAGE_SIZE)))
                .andExpect(status().isOk());
    }

    @Test
    void testPostApprovalRecord() throws Exception {
        //post valid approval
        mvc.perform(post("/records/approvals")
                .requestAttr("userId", VALID_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(VALID_APPROVAL)))
                .andExpect(status().isCreated());

        //post FROM_USER_NOT_EXIST approval
        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
                .when(approvalRecordService)
                .postApprovalRecord(NOT_EXIST_USER_ID, VALID_APPROVAL);

        mvc.perform(post("/records/approvals")
                .requestAttr("userId", NOT_EXIST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(VALID_APPROVAL)))
                .andExpect(status().isNotFound());

        //post TO_USER_NOT_EXIST approval
        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
                .when(approvalRecordService)
                .postApprovalRecord(VALID_USER_ID, TO_USER_NOT_EXIST_APPROVAL);
        mvc.perform(post("/records/approvals")
                .requestAttr("userId", VALID_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TO_USER_NOT_EXIST_APPROVAL)))
                .andExpect(status().isNotFound());

        //post COMMENT_NOT_EXIST approval
        Mockito.doThrow(new PostException(PostException.PostExceptionType.COMMENT_NOT_EXIST))
                .when(approvalRecordService)
                .postApprovalRecord(VALID_USER_ID, COMMENT_NOT_EXIST_APPROVAL);

        mvc.perform(post("/records/approvals")
                .requestAttr("userId", VALID_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(COMMENT_NOT_EXIST_APPROVAL)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteApprovalRecord() throws Exception {
        //delete normal approval
        mvc.perform(delete("/records/approvals")
                .requestAttr("userId", VALID_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(VALID_APPROVAL)))
                .andExpect(status().isNoContent());

        //delete COMMENT_NOT_EXIST approval
        Mockito.doThrow(new PostException(PostException.PostExceptionType.COMMENT_NOT_EXIST))
                .when(approvalRecordService)
                .deleteApprovalRecord(VALID_USER_ID, COMMENT_NOT_EXIST_APPROVAL);

        mvc.perform(delete("/records/approvals")
                .requestAttr("userId", VALID_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(COMMENT_NOT_EXIST_APPROVAL)))
                .andExpect(status().isNotFound());

        //delete TO_USER_NOT_EXIST approval
        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
                .when(approvalRecordService)
                .deleteApprovalRecord(VALID_USER_ID, TO_USER_NOT_EXIST_APPROVAL);

        mvc.perform(delete("/records/approvals")
                .requestAttr("userId", VALID_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TO_USER_NOT_EXIST_APPROVAL)))
                .andExpect(status().isNotFound());

        //delete FROM_USER_NOT_EXIST approval
        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
                .when(approvalRecordService)
                .deleteApprovalRecord(NOT_EXIST_USER_ID, VALID_APPROVAL);

        mvc.perform(delete("/records/approvals")
                .requestAttr("userId", NOT_EXIST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(VALID_APPROVAL)))
                .andExpect(status().isNotFound());
    }
}