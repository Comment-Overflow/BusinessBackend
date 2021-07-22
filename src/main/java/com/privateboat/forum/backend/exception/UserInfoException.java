package com.privateboat.forum.backend.exception;


import java.util.HashMap;
import java.util.Map;

public class UserInfoException extends Exception {
    public enum UserInfoExceptionType {
        USER_NOT_EXIST
    }

    private static final Map<UserInfoExceptionType, String> map = new HashMap<>() {{
        put(UserInfoExceptionType.USER_NOT_EXIST, "用户不存在！");
    }};

    public UserInfoException(UserInfoExceptionType userInfoExceptionType) {
        super(map.get(userInfoExceptionType));
    }
}
