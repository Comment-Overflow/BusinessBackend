package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatDAO extends JpaRepository<Chat, Long> {
    @Query(value = "SELECT * FROM CHAT WHERE user_id = ?1 AND chatter_id = ?2", nativeQuery = true)
    Optional<Chat> findByUserIdAndChatterId(Long userId, Long chatterId);

    @Query(value = "SELECT * FROM CHAT WHERE user_id = ?1", nativeQuery = true)
    List<Chat> findAllByUserId(Long userId);
}
