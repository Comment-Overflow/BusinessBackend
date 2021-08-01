package com.privateboat.forum.backend.exception;


import java.util.HashMap;
import java.util.Map;

public class UserInfoException extends RuntimeException {
    public enum UserInfoExceptionType {
        USER_NOT_EXIST
    }

    private final UserInfoExceptionType type;

    private static final Map<UserInfoExceptionType, String> map = new HashMap<>() {{
        put(UserInfoExceptionType.USER_NOT_EXIST, "用户不存在！");
    }};

    public UserInfoExceptionType getType() {
        return type;
    }

    public UserInfoException(UserInfoExceptionType userInfoExceptionType) {
        super(map.get(userInfoExceptionType));
        type = userInfoExceptionType;
    }
}
