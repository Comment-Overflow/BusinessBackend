package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatDAO extends JpaRepository<Chat, Long> {
    @Query(value = "SELECT * FROM CHAT WHERE user_id = ?1 AND chatter_id = ?2 LIMIT 1", nativeQuery = true)
    Optional<Chat> findByUserIdAndChatterId(Long userId, Long chatterId);

    List<Chat> findAllByUserIdOrderByLastMessage_TimeDesc(Long userId);

    @Query(value = "SELECT SUM(unread_count) FROM CHAT WHERE user_id = ?1", nativeQuery = true)
    Integer sumUnreadByUserId(Long userId);

    @Modifying
    @Query(value = "DELETE FROM CHAT WHERE user_id = ?1 AND unread_count = 0", nativeQuery = true)
    void deleteAllReadChatsByUserId(Long userId);

    @Modifying
    @Query(value = "DELETE FROM CHAT WHERE user_id = ?1 AND chatter_id = ?2", nativeQuery = true)
    void deleteChatByUserIdAndChatterId(Long userId, Long chatterId);
}
