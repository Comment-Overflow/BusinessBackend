package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.MessageDAO;
import com.privateboat.forum.backend.entity.Message;
import com.privateboat.forum.backend.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final MessageDAO messageDAO;

    public Message save(Message message) {
        return messageDAO.save(message);
    }

    public Page<Message> findByUserIdOrChatterId(Long userId, Long chatterId, Pageable pageable) {
        return messageDAO.findBySender_IdAndReceiver_IdOrSender_IdAndReceiver_IdOrderByTimeDesc(userId, chatterId, chatterId, userId, pageable);
    }
}
