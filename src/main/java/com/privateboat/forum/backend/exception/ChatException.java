package com.privateboat.forum.backend.exception;

import java.util.HashMap;
import java.util.Map;

public class ChatException extends RuntimeException {
    public enum ChatExceptionType {
        CHAT_NOT_EXIST,
    }

    private static final Map<ChatException.ChatExceptionType, String> map = new HashMap<>() {{
        put(ChatExceptionType.CHAT_NOT_EXIST, "聊天不存在。");
    }};

    public ChatException(PostException.PostExceptionType postExceptionType) {
        super(map.get(postExceptionType));
    }
}
