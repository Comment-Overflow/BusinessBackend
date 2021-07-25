package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.response.MessageDTO;
import com.privateboat.forum.backend.entity.Message;
import org.springframework.data.domain.Page;


public interface MessageService {

    void sendTextMessage(String uuid, Long senderId, Long receiverId, String content);

    //    void sendImageMessage(String uuid, Long senderId, Long receiverId, )
    Page<MessageDTO> getChatHistory(Long userId, Long chatterId, Integer pageNum, Integer pageSize);
}
