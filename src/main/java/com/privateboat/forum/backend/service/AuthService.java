package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.response.LoginDTO;
import com.privateboat.forum.backend.exception.AuthException;

public interface AuthService {
    void register(String email, String password) throws AuthException;

    LoginDTO login(String email, String password) throws AuthException;

    void verifyAuth(Long userId, String password) throws AuthException;

    LoginDTO refreshToken(Long userId);
}
