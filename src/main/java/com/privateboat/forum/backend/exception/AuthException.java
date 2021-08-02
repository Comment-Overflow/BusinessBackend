package com.privateboat.forum.backend.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class AuthException extends RuntimeException {
    private final AuthExceptionType type;

    public enum AuthExceptionType {
        DUPLICATE_EMAIL, WRONG_EMAIL, WRONG_PASSWORD, EXPIRED_EMAIL_TOKEN, WRONG_EMAIL_TOKEN
    }

    private static final Map<AuthExceptionType, String> map = new HashMap<>() {{
        put(AuthExceptionType.DUPLICATE_EMAIL, "该邮箱已被注册!");
        put(AuthExceptionType.WRONG_EMAIL, "用户不存在!");
        put(AuthExceptionType.WRONG_PASSWORD, "密码错误!");
        put(AuthExceptionType.EXPIRED_EMAIL_TOKEN, "邮箱验证码已过期!");
        put(AuthExceptionType.WRONG_EMAIL_TOKEN, "验证码错误!");
    }};

    public AuthException(AuthExceptionType authExceptionType) {
        super(map.get(authExceptionType));
        this.type = authExceptionType;
    }
}
