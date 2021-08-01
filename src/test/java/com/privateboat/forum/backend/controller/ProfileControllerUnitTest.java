package com.privateboat.forum.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateboat.forum.backend.dto.response.ProfileDTO;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.exception.ProfileException;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.interceptor.JWTInterceptor;
import com.privateboat.forum.backend.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;


import static com.privateboat.forum.backend.fakedata.Profile.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    void getProfile() throws Exception {
        //valid getProfile
        Mockito.when(profileService.getProfile(VALID_USER_ID, VALID_USER_ID))
                .thenReturn(PROFILE_DTO);

        mvc.perform(get("/profiles/1")
                .requestAttr("userId", VALID_USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(PROFILE_DTO)));

        //OTHER_USER_NOT_EXIST getProfile
        Mockito.doThrow(new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST))
                .when(profileService)
                .getProfile(VALID_USER_ID, NOT_EXIST_OTHER_USER_ID);

        mvc.perform(get("/profiles/2")
                .requestAttr("userId", VALID_USER_ID))
                .andExpect(status().isNotFound());
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

        Mockito.when(profileService.putProfile(VALID_USER_ID, VALID_PROFILE_SETTING_REQUEST_DTO))
                .thenReturn(ret);

        mvc.perform(put("/profiles/settings")
                .requestAttr("userId", VALID_USER_ID)
                .param("userName", "wxp")
                .param("brief", "hello world")
                .param("gender", "男"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(ret)));

        Mockito.doThrow(new ProfileException(ProfileException.ProfileExceptionType.GENDER_NOT_VALID))
                .when(profileService)
                .putProfile(VALID_USER_ID, GENDER_NOT_VALID_PROFILE_SETTING_REQUEST_DTO);

        mvc.perform(put("/profiles/settings")
                .requestAttr("userId", VALID_USER_ID)
                .param("userName", "wxp")
                .param("brief", "hello world")
                .param("gender", "transition"))
                .andExpect(status().isInternalServerError());
    }
}