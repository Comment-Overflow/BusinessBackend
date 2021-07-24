package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.request.ChatHistoryDTO;
import com.privateboat.forum.backend.entity.Message;
import com.privateboat.forum.backend.exception.UserInfoException;
import org.springframework.data.domain.Page;


public interface MessageService {

    void sendTextMessage(String uuid, Long senderId, Long receiverId, String content);
//    void sendImageMessage(String uuid, Long senderId, Long receiverId, )
    Page<Message> getChatHistory(ChatHistoryDTO chatHistoryDTO);
}
