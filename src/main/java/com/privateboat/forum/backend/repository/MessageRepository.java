package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageRepository {
    Message save(Message message);

    Page<Message> findByUserIdOrChatterId(Long userId, Long chatterId, Pageable pageable);
}
