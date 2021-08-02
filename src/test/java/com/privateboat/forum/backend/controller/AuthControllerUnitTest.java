package com.privateboat.forum.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateboat.forum.backend.dto.request.RegisterDTO;
import com.privateboat.forum.backend.dto.response.LoginDTO;
import com.privateboat.forum.backend.enumerate.UserType;
import com.privateboat.forum.backend.exception.AuthException;
import com.privateboat.forum.backend.interceptor.JWTInterceptor;
import com.privateboat.forum.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.privateboat.forum.backend.fakedata.UserData.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JWTInterceptor jwtInterceptor;

    @BeforeEach
    void setUp() {
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void testRegister() throws Exception {
        RegisterDTO correctDTO = new RegisterDTO(EMAIL, PASSWORD, CORRECT_EMAIL_CODE, CORRECT_EMAIL_CODE);
        RegisterDTO wrongEmailDTO = new RegisterDTO(EMAIL, PASSWORD, WRONG_EMAIL_CODE, CORRECT_EMAIL_CODE);
        RegisterDTO expiredEmailDTO = new RegisterDTO(EMAIL, PASSWORD, EXPIRED_EMAIL_CODE, CORRECT_EMAIL_CODE);

        // Test normal registration.
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(correctDTO)))
                .andExpect(status().isOk());


        Mockito.doThrow(new AuthException(AuthException.AuthExceptionType.DUPLICATE_EMAIL))
                .when(authService)
                .register(correctDTO.getEmail(),
                        correctDTO.getPassword(),
                        correctDTO.getUserCode(),
                        correctDTO.getEmailToken());

        // Test duplicate email,
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(correctDTO))
        ).andExpect(status().isConflict());

        // Test wrong email code.
        Mockito.doThrow(new AuthException(AuthException.AuthExceptionType.WRONG_EMAIL_TOKEN))
                .when(authService)
                .register(wrongEmailDTO.getEmail(),
                        wrongEmailDTO.getPassword(),
                        wrongEmailDTO.getUserCode(),
                        wrongEmailDTO.getEmailToken());

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongEmailDTO))
        ).andExpect(status().isUnauthorized());

        // Test expired email code.
        Mockito.doThrow(new AuthException(AuthException.AuthExceptionType.WRONG_EMAIL_TOKEN))
                .when(authService)
                .register(expiredEmailDTO.getEmail(),
                        expiredEmailDTO.getPassword(),
                        expiredEmailDTO.getUserCode(),
                        expiredEmailDTO.getEmailToken());

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expiredEmailDTO))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void testLogin() throws Exception {
        com.privateboat.forum.backend.dto.request.LoginDTO successfulRequestDTO
                = new com.privateboat.forum.backend.dto.request.LoginDTO(EMAIL, PASSWORD);
        com.privateboat.forum.backend.dto.response.LoginDTO successfulResponseDTO
                = new LoginDTO(FAKE_TOKEN, USER_ID, EMAIL, null, UserType.USER);
        com.privateboat.forum.backend.dto.request.LoginDTO failRequestDTO
                = new com.privateboat.forum.backend.dto.request.LoginDTO(EMAIL, WRONG_PASSWORD);

        // Test successful login.
        given(authService.login(EMAIL, PASSWORD)).willReturn(successfulResponseDTO);
        mvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(successfulRequestDTO))
        ).andExpect(status().isOk()).andExpect(
                content().json(
                        objectMapper.writeValueAsString(successfulResponseDTO)
                )
        );

        // Test wrong password.
        Mockito.doThrow(new AuthException(AuthException.AuthExceptionType.WRONG_PASSWORD))
                .when(authService)
                .login(EMAIL, WRONG_PASSWORD);
        mvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(failRequestDTO))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void testAutoLogin() throws Exception {
        LoginDTO loginDTO = new LoginDTO(FAKE_TOKEN, USER_ID, EMAIL, null, UserType.USER);
        given(authService.refreshToken(USER_ID)).willReturn(loginDTO);
        mvc.perform(get("/sessions").
                requestAttr("userId", USER_ID)
        ).andExpect(status().isOk()).andExpect(content().
                json(objectMapper.writeValueAsString(loginDTO)
                )
        );
    }

    @Test
    void testSendEmail() throws Exception {
        mvc.perform(post("/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(EMAIL)
        ).andExpect(status().isOk());
    }
}
