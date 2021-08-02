package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.ChatDAO;
import com.privateboat.forum.backend.entity.Chat;
import com.privateboat.forum.backend.repository.ChatRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Getter
@Setter
@AllArgsConstructor
public class ChatRepositoryImpl implements ChatRepository {

    private final ChatDAO chatDAO;

    @Override
    public Chat save(Chat chat) {
        return chatDAO.save(chat);
    }

    @Override
    public Optional<Chat> findByUserIdAndChatterId(Long userId, Long chatterId) {
        return chatDAO.findByUserIdAndChatterId(userId, chatterId);
    }

    @Override
    public List<Chat> findAllByUserId(Long userId) {
        return chatDAO.findAllByUserIdOrderByLastMessage_TimeDesc(userId);
    }

    @Override
    public Integer sumUnreadByUserId(Long userId) {
        return chatDAO.sumUnreadByUserId(userId);
    }

    @Override
    public void deleteAllReadChatsByUserId(Long userId) {
        chatDAO.deleteAllReadChatsByUserId(userId);
    }

    @Override
    public void deleteChatByUserIdAndChatterId(Long userId, Long chatterId) {
        chatDAO.deleteChatByUserIdAndChatterId(userId, chatterId);
    }
}
