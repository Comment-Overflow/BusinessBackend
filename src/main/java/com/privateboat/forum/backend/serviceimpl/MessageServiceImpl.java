package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.request.ChatHistoryDTO;
import com.privateboat.forum.backend.entity.Message;
import com.privateboat.forum.backend.enumerate.MessageType;
import com.privateboat.forum.backend.repository.MessageRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;

@Service
@Transactional
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserInfoRepository userInfoRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendTextMessage(String uuid, Long senderId, Long receiverId, String content) {

        String receiverChannel = "/queue/private";
        String notifyChannel = "/notify/" + uuid;
        Message message = new Message();

        try {
            // Save message to the database.
            message.setId(uuid);
            message.setSender(userInfoRepository.getById(senderId));
            message.setReceiver(userInfoRepository.getById(receiverId));
            message.setTime(new Timestamp(System.currentTimeMillis()));
            message.setType(MessageType.TEXT);
            message.setContent(content);
            messageRepository.save(message);

            // Send to the receiver via socket.
            simpMessagingTemplate.convertAndSendToUser(receiverId.toString(), receiverChannel, message.toString());

        } catch (Exception e) {
            // Send the error to the sender via socket.
            simpMessagingTemplate.convertAndSend(notifyChannel, "error");
        }

        // Send the success acknowledgment to the sender via socket.
        simpMessagingTemplate.convertAndSend(notifyChannel, message.toString());
    }

    @Override
    public Page<Message> getChatHistory(ChatHistoryDTO chatHistoryDTO) {
        Pageable pageable = PageRequest.of(chatHistoryDTO.getPageNum(), chatHistoryDTO.getPageSize());
        return messageRepository.findByUserIdOrChatterId(chatHistoryDTO.getUserId(), chatHistoryDTO.getChatterId(), pageable);
    }

}
