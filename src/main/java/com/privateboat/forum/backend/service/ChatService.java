package com.privateboat.forum.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privateboat.forum.backend.dto.request.ImageMessageDTO;
import com.privateboat.forum.backend.dto.request.TextMessageDTO;
import com.privateboat.forum.backend.dto.response.ChatDTO;
import com.privateboat.forum.backend.dto.response.MessageDTO;
import com.privateboat.forum.backend.entity.Chat;
import com.privateboat.forum.backend.entity.Message;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;


public interface ChatService {

    Timestamp sendTextMessage(Long senderId, TextMessageDTO textMessageDTO) throws JsonProcessingException;

    Timestamp sendImageMessage(Long senderId, ImageMessageDTO imageMessageDTO) throws JsonProcessingException;

    Page<MessageDTO> getChatHistory(Long userId, Long chatterId, Integer pageNum, Integer pageSize);

    void updateSeenBy(Long userId, Long chatterId);

    List<ChatDTO> getRecentChats(Long userId);

    Integer getTotalUnreadCount(Long userId);

    void deleteAllReadChat(Long userId);

    void deleteChat(Long userId, Long chatterId);
}
