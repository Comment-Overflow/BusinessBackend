package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageDAO extends JpaRepository<Message, Long> {
    Page<Message> findBySender_IdAndReceiver_IdOrSender_IdAndReceiver_IdOrderByTimeDesc(
            Long senderA, Long receiverA, Long senderB, Long receiverB, Pageable pageable);
}
