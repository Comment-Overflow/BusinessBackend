package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.response.LoginDTO;
import com.privateboat.forum.backend.exception.AuthException;

public interface AuthService {
    void register(String email, String password, String emailToken) throws AuthException;

    LoginDTO login(String email, String rawPassword) throws AuthException;

    void verifyAuth(Long userId, String enCodedPassword) throws AuthException;

    LoginDTO refreshToken(Long userId);

    String sendEmail(String email);
}
