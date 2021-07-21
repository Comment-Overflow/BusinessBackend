package com.privateboat.forum.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class AuthException extends Exception {
    public enum AuthExceptionType {
        DUPLICATE_EMAIL, WRONG_EMAIL, WRONG_PASSWORD
    }

    private static final Map<AuthExceptionType, String> map = new HashMap<>() {{
        put(AuthExceptionType.DUPLICATE_EMAIL, "该邮箱已被注册!");
        put(AuthExceptionType.WRONG_EMAIL, "用户不存在!");
        put(AuthExceptionType.WRONG_PASSWORD, "密码错误!");
    }};

    public AuthException(AuthExceptionType authExceptionType) {
        super(map.get(authExceptionType));
    }
}
