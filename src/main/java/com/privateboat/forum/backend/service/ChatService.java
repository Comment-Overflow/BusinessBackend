package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.request.ImageMessageDTO;
import com.privateboat.forum.backend.dto.response.ChatDTO;
import com.privateboat.forum.backend.dto.response.MessageDTO;
import com.privateboat.forum.backend.entity.Chat;
import com.privateboat.forum.backend.entity.Message;
import org.springframework.data.domain.Page;

import java.util.List;


public interface ChatService {

    void sendTextMessage(String uuid, Long senderId, Long receiverId, String content);

    MessageDTO sendImageMessage(Long senderId, ImageMessageDTO imageMessageDTO);

    Page<MessageDTO> getChatHistory(Long userId, Long chatterId, Integer pageNum, Integer pageSize);

    void updateSeenBy(Long userId, Long chatterId);

    List<ChatDTO> getRecentChats(Long userId);
}
