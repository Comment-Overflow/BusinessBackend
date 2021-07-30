package com.privateboat.forum.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.interceptor.JWTInterceptor;
import com.privateboat.forum.backend.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;


import static com.privateboat.forum.backend.fakedata.Profile.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProfileController.class)
class ProfileControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProfileService profileService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JWTInterceptor jwtInterceptor;

    @BeforeEach
    void setUp() {
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void getProfile() {
    }

    @Test
    void putProfileTest() throws Exception {
        UserInfo.UserNameAndAvatarUrl ret = new UserInfo.UserNameAndAvatarUrl() {
            @Override
            public String getUserName() {
                return "wxp";
            }

            @Override
            public String getAvatarUrl() {
                return "url.com";
            }
        };

        given(profileService.putProfile(USER_ID, profileSettingRequestDTO)).willReturn(ret);

        mvc.perform(put("/profiles/settings")
                .requestAttr("userId", USER_ID)
                .param("userName", "wxp")
                .param("brief", "hello world")
                .param("gender", "男"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(ret)));
    }
}