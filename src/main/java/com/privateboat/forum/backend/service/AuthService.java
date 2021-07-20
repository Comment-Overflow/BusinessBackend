package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.exception.AuthException;

public interface AuthService {
    void register(String email, String password) throws AuthException;

    String login(String email, String password) throws AuthException;
}
