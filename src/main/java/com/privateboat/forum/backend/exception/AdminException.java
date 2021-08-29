package com.privateboat.forum.backend.exception;

import java.util.HashMap;
import java.util.Map;

public class AdminException extends RuntimeException {
    public enum AdminExceptionType {
        OPERATOR_NOT_ADMIN, INVALID_SILENCE_TARGET, INVALID_FREE_TARGET
    }

    private final AdminExceptionType type;

    private static final Map<AdminExceptionType, String> map = new HashMap<AdminExceptionType, String>() {{
        put(AdminExceptionType.OPERATOR_NOT_ADMIN, "非管理员越权操作。");
        put(AdminExceptionType.INVALID_SILENCE_TARGET, "这个用户不能被禁言!");
        put(AdminExceptionType.INVALID_FREE_TARGET, "不是一个合法的被释放用户！");
    }};

    public AdminException(AdminExceptionType adminExceptionType) {
        super(map.get(adminExceptionType));
        this.type = adminExceptionType;
    }

    public AdminExceptionType getType() {
        return type;
    }
}
