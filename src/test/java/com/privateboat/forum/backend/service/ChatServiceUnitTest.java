package com.privateboat.forum.backend.service;

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
import com.privateboat.forum.backend.repository.ChatRepository;
import com.privateboat.forum.backend.repository.MessageRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.serviceimpl.ChatServiceImpl;
import com.privateboat.forum.backend.util.ImageUtil;
import com.privateboat.forum.backend.util.OffsetBasedPageRequest;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class ChatServiceUnitTest {

    static private final Long USER_ID = 1L;
    static private final Long CHATTER_ID = 2L;
    static private final Integer TOTAL_UNREAD = 3;
    static private final Integer PAGE_OFFSET = 6;
    static private final Integer PAGE_SIZE = 10;
    static private final UserInfo USER_INFO;
    static private final UserInfo CHATTER_INFO;
    static private final String TEXT_MESSAGE_CONTENT = "Hi";
    static private final String IMAGE_MESSAGE_CONTENT = "chat/";
    static private final String TIME = "30/07/2021 09:31:00:000";
    static private final Message TEXT_MESSAGE;
    static private final Message IMAGE_MESSAGE;
    static private final Page<Message> MESSAGE_PAGE;

    static {
        // Make the time still.
        final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
        Date fixedDateTime = null;
        try {
            fixedDateTime = DATE_FORMATTER.parse(TIME);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateTimeUtils.setCurrentMillisFixed(Objects.requireNonNull(fixedDateTime).getTime());

        // Mock user info.
        USER_INFO = new UserInfo();
        CHATTER_INFO = new UserInfo();
        USER_INFO.setId(USER_ID);
        CHATTER_INFO.setId(CHATTER_ID);

        // Mock a text message.
        TEXT_MESSAGE = new Message(8L, USER_INFO, CHATTER_INFO,
                new Timestamp(DateTimeUtils.currentTimeMillis()),
                MessageType.TEXT, TEXT_MESSAGE_CONTENT);

        // Mock an image message.
        IMAGE_MESSAGE = new Message(8L, USER_INFO, CHATTER_INFO,
                new Timestamp(DateTimeUtils.currentTimeMillis()),
                MessageType.IMAGE, IMAGE_MESSAGE_CONTENT);

        // Mock a page.
        List<Message> messages = new ArrayList<>();
        for (long i = 1; i <= 20; ++i) {
            Message message = new Message(i, USER_INFO, CHATTER_INFO,
                    new Timestamp(DateTimeUtils.currentTimeMillis()),
                    MessageType.TEXT, TEXT_MESSAGE_CONTENT);
            messages.add(message);
        }
        Pageable pageable = new OffsetBasedPageRequest(PAGE_OFFSET, PAGE_SIZE);
        MESSAGE_PAGE = new PageImpl<>(messages, pageable, messages.size());
    }

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private ProjectionFactory projectionFactory;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private Environment environment;

    @InjectMocks
    private ChatServiceImpl chatService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Environment
        Mockito.when(environment.getProperty("com.privateboat.forum.backend.image-base-url"))
                .thenReturn("");

        // MessageRepository
        Mockito.when(messageRepository.save(Mockito.any(Message.class))).thenReturn(TEXT_MESSAGE);
        Mockito.when(messageRepository.findByUserIdOrChatterId(USER_ID, CHATTER_ID,
                new OffsetBasedPageRequest(PAGE_OFFSET, PAGE_SIZE)))
                .thenReturn(MESSAGE_PAGE);

        // ChatRepository
        Mockito.when(chatRepository.sumUnreadByUserId(USER_ID)).thenReturn(TOTAL_UNREAD);
        Mockito.when(chatRepository.findAllByUserId(USER_ID)).thenReturn(new ArrayList<>());
        Mockito.doNothing().when(chatRepository).deleteAllReadChatsByUserId(USER_ID);
        Mockito.doNothing().when(chatRepository).deleteChatByUserIdAndChatterId(USER_ID, CHATTER_ID);

        // UserInfoRepository
        Mockito.when(userInfoRepository.getById(USER_ID)).thenReturn(USER_INFO);
        Mockito.when(userInfoRepository.getById(CHATTER_ID)).thenReturn(CHATTER_INFO);

    }

    @Test
    void sendTextMessage() throws JsonProcessingException {

        // Build runtime data.
        Chat originalUserChat = new Chat(1L, USER_INFO, CHATTER_INFO, TEXT_MESSAGE, 0);
        Chat originalChatterChat = new Chat(2L, CHATTER_INFO, USER_INFO, TEXT_MESSAGE, 0);
        Chat savedUserChat = new Chat(1L, USER_INFO, CHATTER_INFO, TEXT_MESSAGE, 0);
        Chat savedChatterChat = new Chat(2L, CHATTER_INFO, USER_INFO, TEXT_MESSAGE, 1);

        // Mock runtime operations.
        Mockito.when(chatRepository.findByUserIdAndChatterId(USER_ID, CHATTER_ID))
                .thenReturn(Optional.of(originalUserChat));
        Mockito.when(chatRepository.findByUserIdAndChatterId(CHATTER_ID, USER_ID))
                .thenReturn(Optional.of(originalChatterChat));
        Mockito.when(chatRepository.save(originalUserChat)).thenReturn(savedUserChat);
        Mockito.when(chatRepository.save(originalChatterChat)).thenReturn(savedChatterChat);

        // Do the test.
        TextMessageDTO textMessageDTO = new TextMessageDTO(CHATTER_ID, TEXT_MESSAGE_CONTENT);
        Timestamp time = chatService.sendTextMessage(USER_ID, textMessageDTO);

        Mockito.verify(userInfoRepository).getById(USER_ID);
        Mockito.verify(userInfoRepository).getById(CHATTER_ID);

        Message message = new Message(USER_INFO, CHATTER_INFO,
                new Timestamp(DateTimeUtils.currentTimeMillis()),
                MessageType.TEXT, TEXT_MESSAGE_CONTENT);
        Mockito.verify(messageRepository).save(message);

        Mockito.verify(chatRepository).findByUserIdAndChatterId(USER_ID, CHATTER_ID);
        Mockito.verify(chatRepository).save(originalUserChat);
        Mockito.verify(chatRepository).save(originalChatterChat);

        Assertions.assertEquals(time, new Timestamp(DateTimeUtils.currentTimeMillis()));
    }

    @Test
    void sendImageMessage() throws JsonProcessingException {

        // Build runtime data.
        Chat originalUserChat = new Chat(1L, USER_INFO, CHATTER_INFO, IMAGE_MESSAGE, 0);
        Chat originalChatterChat = new Chat(2L, CHATTER_INFO, USER_INFO, IMAGE_MESSAGE, 0);
        Chat savedUserChat = new Chat(1L, USER_INFO, CHATTER_INFO, IMAGE_MESSAGE, 0);
        Chat savedChatterChat = new Chat(2L, CHATTER_INFO, USER_INFO, IMAGE_MESSAGE, 1);

        // Mock runtime operations.
        Mockito.when(chatRepository.findByUserIdAndChatterId(USER_ID, CHATTER_ID))
                .thenReturn(Optional.of(originalUserChat));
        Mockito.when(chatRepository.findByUserIdAndChatterId(CHATTER_ID, USER_ID))
                .thenReturn(Optional.of(originalChatterChat));
        Mockito.when(chatRepository.save(originalUserChat)).thenReturn(savedUserChat);
        Mockito.when(chatRepository.save(originalChatterChat)).thenReturn(savedChatterChat);

        // Do the test.
        // Mock static class ImageUtil
        try(MockedStatic<ImageUtil> mockedImageUtil = Mockito.mockStatic(ImageUtil.class)) {
            mockedImageUtil.when(() -> ImageUtil.uploadImage(
                    ArgumentMatchers.any(),
                    ArgumentMatchers.any(),
                    ArgumentMatchers.any()))
                    .thenReturn(true);
            mockedImageUtil.when(() -> ImageUtil.getNewImageName(ArgumentMatchers.any()))
                    .thenReturn("");

            ImageMessageDTO imageMessageDTO = new ImageMessageDTO();
            imageMessageDTO.setReceiverId(CHATTER_ID);
            Timestamp time = chatService.sendImageMessage(USER_ID, imageMessageDTO);

            Mockito.verify(userInfoRepository).getById(USER_ID);
            Mockito.verify(userInfoRepository).getById(CHATTER_ID);

            Message message = new Message(USER_INFO, CHATTER_INFO,
                    new Timestamp(DateTimeUtils.currentTimeMillis()),
                    MessageType.IMAGE, IMAGE_MESSAGE_CONTENT);
            Mockito.verify(messageRepository).save(message);

            Mockito.verify(chatRepository).findByUserIdAndChatterId(USER_ID, CHATTER_ID);
            Mockito.verify(chatRepository).save(originalUserChat);
            Mockito.verify(chatRepository).save(originalChatterChat);

            Assertions.assertEquals(time, new Timestamp(DateTimeUtils.currentTimeMillis()));
        }
    }

    @Test
    void updateSeenBy() {
        // Build runtime data.
        Chat originalUserChat = new Chat(1L, USER_INFO, CHATTER_INFO, IMAGE_MESSAGE, 5);
        Chat updatedUserChat = new Chat(1L, USER_INFO, CHATTER_INFO, IMAGE_MESSAGE, 0);

        // Mock runtime operations.
        Mockito.when(chatRepository.findByUserIdAndChatterId(USER_ID, CHATTER_ID))
                .thenReturn(Optional.of(originalUserChat));
        Mockito.when(chatRepository.save(originalUserChat))
                .thenReturn(updatedUserChat);

        // Do the test.
        chatService.updateSeenBy(USER_ID, CHATTER_ID);
        Mockito.verify(chatRepository).findByUserIdAndChatterId(USER_ID, CHATTER_ID);
        Mockito.verify(chatRepository).save(originalUserChat);
    }

    @Test
    void getChatHistory() {
        Page<MessageDTO> messageDTOs = chatService.getChatHistory(
                USER_ID, CHATTER_ID, PAGE_OFFSET, PAGE_SIZE);
        Mockito.verify(messageRepository).findByUserIdOrChatterId(
                USER_ID, CHATTER_ID, new OffsetBasedPageRequest(PAGE_OFFSET, PAGE_SIZE));
        Assertions.assertEquals(messageDTOs,
                MESSAGE_PAGE.map(message ->
                        new MessageDTO(
                                projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, message.getSender()),
                                projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, message.getReceiver()),
                                message.getTime(), message.getType(), message.getContent())
                )
        );
    }

    @Test
    void getRecentChats() {
        List<ChatDTO> chatDTOs = chatService.getRecentChats(USER_ID);
        Mockito.verify(chatRepository).findAllByUserId(USER_ID);
        Assertions.assertEquals(chatDTOs, new ArrayList<ChatDTO>());
    }

    @Test
    void getTotalUnreadCount() {
        Integer totalUnreadCount = chatService.getTotalUnreadCount(USER_ID);
        Mockito.verify(chatRepository).sumUnreadByUserId(USER_ID);
        Assertions.assertEquals(totalUnreadCount, TOTAL_UNREAD);
    }

    @Test
    void deleteAllReadChat() {
        chatService.deleteAllReadChat(USER_ID);
        Mockito.verify(chatRepository).deleteAllReadChatsByUserId(USER_ID);
    }

    @Test
    void deleteChat() {
        chatService.deleteChat(USER_ID, CHATTER_ID);
        Mockito.verify(chatRepository).deleteChatByUserIdAndChatterId(USER_ID, CHATTER_ID);
    }
}