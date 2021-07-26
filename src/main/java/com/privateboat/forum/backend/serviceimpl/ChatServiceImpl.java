package com.privateboat.forum.backend.serviceimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateboat.forum.backend.dto.request.ImageMessageDTO;
import com.privateboat.forum.backend.dto.response.ChatDTO;
import com.privateboat.forum.backend.dto.response.MessageDTO;
import com.privateboat.forum.backend.entity.Chat;
import com.privateboat.forum.backend.entity.Message;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.MessageType;
import com.privateboat.forum.backend.exception.ChatException;
import com.privateboat.forum.backend.repository.ChatRepository;
import com.privateboat.forum.backend.repository.MessageRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.ChatService;
import com.privateboat.forum.backend.util.Constant;
import com.privateboat.forum.backend.util.ImageUtil;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ObjectMapper objectMapper;
    private final ProjectionFactory projectionFactory;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserInfoRepository userInfoRepository;

    private final Environment environment;
    static private final String imageFolderName = "chat/";
    static private final String receiverChannel = "/queue/private";

    @Override
    public void sendTextMessage(String uuid, Long senderId, Long receiverId, String content) {

        String notifyChannel = "/notify/" + uuid;
        Timestamp time = new Timestamp(System.currentTimeMillis());
        MessageType type = MessageType.TEXT;
        MessageDTO messageToSend;

        try {
            UserInfo senderInfo = userInfoRepository.getById(senderId);
            UserInfo receiverInfo = userInfoRepository.getById(receiverId);

            // Save message to the database.
            Message message = new Message(senderInfo, receiverInfo, time, type, content);
            messageRepository.save(message);

            // Update chat in database.
            updateChatOnNewMessage(message);

            // Send the message to the receiver via socket.
            messageToSend = new MessageDTO(
                    projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, senderInfo),
                    projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, senderInfo),
                    time, type, content);
            String jsonMessage = objectMapper.writeValueAsString(messageToSend);
            simpMessagingTemplate.convertAndSendToUser(receiverId.toString(), receiverChannel, jsonMessage);

        } catch (RuntimeException | JsonProcessingException e) {
            // Send the error to the sender via socket.
            System.out.println(e.getMessage());
            simpMessagingTemplate.convertAndSend(notifyChannel, "error");
            return;
        }

        // Send the success acknowledgment to the sender via socket.
        simpMessagingTemplate.convertAndSend(notifyChannel, time.toString());
    }

    @Override
    public MessageDTO sendImageMessage(Long senderId, ImageMessageDTO imageMessageDTO) throws JsonProcessingException {
        System.out.println("enter sendImageMessage");
        // Upload the image.
        MultipartFile imageFile = imageMessageDTO.getImageFile();
        String newName = ImageUtil.getNewImageName(imageFile);
        if (!ImageUtil.uploadImage(imageFile, newName, imageFolderName))
            throw new ChatException(ChatException.ChatExceptionType.SEND_IMAGE_FAILED);

        // Save message to the database.
        String imageUrl = environment.getProperty("com.privateboat.forum.backend.image-base-url") + imageFolderName + newName;
        Long receiverId = imageMessageDTO.getReceiverId();
        UserInfo senderInfo = userInfoRepository.getById(senderId);
        UserInfo receiverInfo = userInfoRepository.getById(receiverId);
        Timestamp time = new Timestamp(System.currentTimeMillis());
        MessageType type = MessageType.IMAGE;
        Message message = new Message(senderInfo, receiverInfo, time, type, imageUrl);
        messageRepository.save(message);

        // Update chat in database.
        updateChatOnNewMessage(message);

        // Send the message to the receiver via socket.
        MessageDTO messageToSend = new MessageDTO(
                projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, senderInfo),
                projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, senderInfo),
                time, type, imageUrl);
        String jsonMessage = objectMapper.writeValueAsString(messageToSend);
        simpMessagingTemplate.convertAndSendToUser(receiverId.toString(), receiverChannel, jsonMessage);

        return messageToSend;
    }

    @Override
    public void updateSeenBy(Long userId, Long chatterId) {
        Optional<Chat> chatOpt = chatRepository.findByUserIdAndChatterId(userId, chatterId);
        if (chatOpt.isPresent()) {
            Chat chat = chatOpt.get();
            chat.setUnreadCount(0);
            chatRepository.save(chat);
        }
    }

    @Override
    public Page<MessageDTO> getChatHistory(Long userId, Long chatterId, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Message> messages = messageRepository.findByUserIdOrChatterId(userId, chatterId, pageable);
        return messages.map((message ->
                new MessageDTO(
                        projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, message.getSender()),
                        projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, message.getReceiver()),
                        message.getTime(), message.getType(), message.getContent())
        ));
    }

    @Override
    public List<ChatDTO> getRecentChats(Long userId) {
        return chatRepository.findAllByUserId(userId).stream().map(chat -> {
                    Message lastMessage = chat.getLastMessage();
                    String content = lastMessage.getType() == MessageType.TEXT
                            ? lastMessage.getContent() : Constant.IMAGE_STRING;
                    return new ChatDTO(
                            projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, chat.getChatter()),
                            content, lastMessage.getTime(), chat.getUnreadCount());
                }
        ).collect(Collectors.toList());
    }

    private void updateChatOnNewMessage(Message message) {
        UserInfo senderInfo = message.getSender();
        UserInfo receiverInfo = message.getReceiver();
        Long senderId = senderInfo.getId();
        Long receiverId = receiverInfo.getId();

        // Update sender chat in database.
        Optional<Chat> senderChatOpt = chatRepository.findByUserIdAndChatterId(senderId, receiverId);
        if (senderChatOpt.isEmpty()) {
            Chat newChat = new Chat(senderInfo, receiverInfo, message, 0);
            chatRepository.save(newChat);
        } else {
            Chat chat = senderChatOpt.get();
            chat.setLastMessage(message);
            chat.setUnreadCount(0);
            chatRepository.save(chat);
        }

        // Update receiver chat in database.
        Optional<Chat> receiverChatOpt = chatRepository.findByUserIdAndChatterId(receiverId, senderId);
        if (receiverChatOpt.isEmpty()) {
            Chat newChat = new Chat(receiverInfo, senderInfo, message, 1);
            chatRepository.save(newChat);
        } else {
            Chat chat = receiverChatOpt.get();
            chat.setLastMessage(message);
            chat.setUnreadCount(chat.getUnreadCount() + 1);
            chatRepository.save(chat);
        }
    }
}
