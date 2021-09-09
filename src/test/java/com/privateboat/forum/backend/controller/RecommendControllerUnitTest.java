package com.privateboat.forum.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateboat.forum.backend.interceptor.JWTInterceptor;
import com.privateboat.forum.backend.service.RecommendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.privateboat.forum.backend.fakedata.Recommend.PAGE_OFFSET;
import static com.privateboat.forum.backend.fakedata.Recommend.VALID_USER_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendController.class)
class RecommendControllerUnitTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private RecommendService recommendService;

    @MockBean
    private JWTInterceptor jwtInterceptor;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void testGetCBRecommendations() throws Exception {
        mvc.perform(get("/recommendations/content_based")
                .requestAttr("userId", VALID_USER_ID)
                .param("pageNum", String.valueOf(PAGE_OFFSET))
                .param("pageSize", String.valueOf(PAGE_OFFSET)))
                .andExpect(status().isOk());
    }

    @Test
    void getCFRecommendations() throws Exception {
        mvc.perform(get("/recommendations/collaborative_filter")
                .requestAttr("userId", VALID_USER_ID)
                .param("pageNum", String.valueOf(PAGE_OFFSET))
                .param("pageSize", String.valueOf(PAGE_OFFSET)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllRecommendations() throws Exception {
        mvc.perform(get("/recommendations/all")
                .requestAttr("userId", VALID_USER_ID)
                .param("pageNum", String.valueOf(PAGE_OFFSET))
                .param("pageSize", String.valueOf(PAGE_OFFSET)))
                .andExpect(status().isOk());
    }
}