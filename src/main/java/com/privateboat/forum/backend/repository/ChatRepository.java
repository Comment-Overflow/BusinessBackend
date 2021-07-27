package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.Chat;

import java.util.List;
import java.util.Optional;

public interface ChatRepository {
    Chat save(Chat chat);
    Optional<Chat> findByUserIdAndChatterId(Long userId, Long chatterId);
    List<Chat> findAllByUserId(Long userId);
    Integer sumUnreadByUserId(Long userId);
    void deleteAllReadChatsByUserId(Long userId);
    void deleteChatByUserIdAndChatterId(Long userId, Long chatterId);
}
