package com.privateboat.forum.backend.exception;

import java.util.HashMap;
import java.util.Map;

public class RabbitMQException extends RuntimeException {
    public enum RabbitMQExceptionType {
        NULL_BEAN
    }

    private final RabbitMQExceptionType type;

    private static final Map<RabbitMQExceptionType, String> map = new HashMap<RabbitMQExceptionType, String>() {{
        put(RabbitMQExceptionType.NULL_BEAN, "实体转换失败！");
    }};

    public RabbitMQException(RabbitMQExceptionType rabbitMQExceptionType) {
        super(map.get(rabbitMQExceptionType));
        this.type = rabbitMQExceptionType;
    }

    public RabbitMQExceptionType getType() {
        return type;
    }
}
