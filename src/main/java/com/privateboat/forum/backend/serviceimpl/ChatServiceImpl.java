package com.privateboat.forum.backend.serviceimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateboat.forum.backend.dto.request.ImageMessageDTO;
import com.privateboat.forum.backend.dto.request.TextMessageDTO;
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
import com.privateboat.forum.backend.util.OffsetBasedPageRequest;
import lombok.AllArgsConstructor;
import org.joda.time.DateTimeUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
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
    public Timestamp sendTextMessage(Long senderId, TextMessageDTO textMessageDTO) throws JsonProcessingException {

        Long receiverId = textMessageDTO.getReceiverId();
        String content = textMessageDTO.getContent();
        Timestamp time = new Timestamp(DateTimeUtils.currentTimeMillis());
        MessageType type = MessageType.TEXT;

        UserInfo senderInfo = userInfoRepository.getById(senderId);
        UserInfo receiverInfo = userInfoRepository.getById(receiverId);

        Message message = new Message(senderInfo, receiverInfo, time, type, content);

        // Save message to the database.
        messageRepository.save(message);

        // Update chat in database.
        updateChatOnNewMessage(message);

        // Send the message to the receiver via socket.
        sendMessageToReceiver(message);

        return time;
    }

    @Override
    public Timestamp sendImageMessage(Long senderId, ImageMessageDTO imageMessageDTO) throws JsonProcessingException {

        // Upload the image.
        MultipartFile imageFile = imageMessageDTO.getImageFile();
        String newName = ImageUtil.getNewImageName(imageFile);
        if (!ImageUtil.uploadImage(imageFile, newName, imageFolderName))
            throw new ChatException(ChatException.ChatExceptionType.SEND_IMAGE_FAILED);

        String imageUrl = environment.getProperty("com.privateboat.forum.backend.image-base-url") + imageFolderName + newName;
        Long receiverId = imageMessageDTO.getReceiverId();
        UserInfo senderInfo = userInfoRepository.getById(senderId);
        UserInfo receiverInfo = userInfoRepository.getById(receiverId);
        Timestamp time = new Timestamp(DateTimeUtils.currentTimeMillis());
        MessageType type = MessageType.IMAGE;

        Message message = new Message(senderInfo, receiverInfo, time, type, imageUrl);

        // Save message to the database.
        messageRepository.save(message);

        // Update chat in database.
        updateChatOnNewMessage(message);

        // Send the message to the receiver via socket.
        sendMessageToReceiver(message);

        return time;
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
    public Page<MessageDTO> getChatHistory(Long userId, Long chatterId, Integer offset, Integer limit) {
        Pageable pageable = new OffsetBasedPageRequest(offset, limit);
        Page<Message> messages = messageRepository.findByUserIdOrChatterId(userId, chatterId, pageable);
        return messages.map(message ->
                new MessageDTO(
                        projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, message.getSender()),
                        projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, message.getReceiver()),
                        message.getTime(), message.getType(), message.getContent())
        );
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

    @Override
    public Integer getTotalUnreadCount(Long userId) {
        Integer unreadCount = chatRepository.sumUnreadByUserId(userId);
        return unreadCount == null ? 0 : unreadCount;
    }

    @Override
    public void deleteAllReadChat(Long userId) {
        chatRepository.deleteAllReadChatsByUserId(userId);
    }

    @Override
    public void deleteChat(Long userId, Long chatterId) {
        chatRepository.deleteChatByUserIdAndChatterId(userId, chatterId);
    }

    private void sendMessageToReceiver(Message message) throws JsonProcessingException {
        MessageDTO messageToSend = new MessageDTO(
                projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, message.getSender()),
                projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, message.getReceiver()),
                message.getTime(), message.getType(), message.getContent());
        String jsonMessage = objectMapper.writeValueAsString(messageToSend);
        simpMessagingTemplate.convertAndSendToUser(
                message.getReceiver().getId().toString(), receiverChannel, jsonMessage);
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
