package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Message;
import com.privateboat.forum.backend.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageDAO extends JpaRepository<Message, Long> {
    Page<Message> findBySender_IdOrReceiver_IdOrderByTimeDesc(Long senderId, Long receiverId, Pageable pageable);

}
