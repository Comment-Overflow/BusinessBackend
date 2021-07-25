package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.response.MessageDTO;
import com.privateboat.forum.backend.entity.Message;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.MessageType;
import com.privateboat.forum.backend.repository.MessageRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.MessageService;
import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;

@Service
@Transactional
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final ProjectionFactory projectionFactory;
    private final MessageRepository messageRepository;
    private final UserInfoRepository userInfoRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendTextMessage(String uuid, Long senderId, Long receiverId, String content) {

        String receiverChannel = "/queue/private";
        String notifyChannel = "/notify/" + uuid;
        MessageDTO messageToSend;

        try {
            UserInfo senderInfo = userInfoRepository.getById(senderId);
            UserInfo receiverInfo = userInfoRepository.getById(receiverId);
            Timestamp time = new Timestamp(System.currentTimeMillis());
            MessageType type = MessageType.TEXT;

            // Save message to the database.
            Message message = new Message(uuid, senderInfo, receiverInfo,
                    time, type, content);
            messageRepository.save(message);

            // Send the message to the receiver via socket.
            messageToSend = new MessageDTO(
                    projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, senderInfo),
                    projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, senderInfo),
                    time, type, content);
            simpMessagingTemplate.convertAndSendToUser(receiverId.toString(), receiverChannel, messageToSend.toString());

        } catch (RuntimeException e) {
            // Send the error to the sender via socket.
            System.out.println(e.getMessage());
            simpMessagingTemplate.convertAndSend(notifyChannel, "error");
            return;
        }

        // Send the success acknowledgment to the sender via socket.
        simpMessagingTemplate.convertAndSend(notifyChannel, messageToSend.toString());
    }

    @Override
    public Page<MessageDTO> getChatHistory(Long userId, Long chatterId, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Message> messages = messageRepository.findByUserIdOrChatterId(userId, chatterId, pageable);
        System.out.println(messages.getContent().get(0).getTime());
        return messages.map((message ->
                new MessageDTO(
                        projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, message.getSender()),
                        projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, message.getReceiver()),
                        message.getTime(), message.getType(), message.getContent())
        ));
    }

}
