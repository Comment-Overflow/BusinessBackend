package com.privateboat.forum.backend.exception;

import java.util.HashMap;
import java.util.Map;

public class ChatException extends RuntimeException {
    public enum ChatExceptionType {
        CHAT_NOT_EXIST, SEND_IMAGE_FAILED
    }

    private static final Map<ChatException.ChatExceptionType, String> map = new HashMap<ChatExceptionType, String>() {{
        put(ChatExceptionType.CHAT_NOT_EXIST, "聊天不存在");
        put(ChatExceptionType.SEND_IMAGE_FAILED, "图片发送失败");
    }};

    public ChatException(ChatExceptionType chatExceptionType) {
        super(map.get(chatExceptionType));
    }
}
