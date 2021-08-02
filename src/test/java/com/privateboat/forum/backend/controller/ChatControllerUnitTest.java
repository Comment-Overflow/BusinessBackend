package com.privateboat.forum.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateboat.forum.backend.dto.request.ImageMessageDTO;
import com.privateboat.forum.backend.dto.request.TextMessageDTO;
import com.privateboat.forum.backend.dto.response.ChatDTO;
import com.privateboat.forum.backend.dto.response.MessageDTO;
import com.privateboat.forum.backend.entity.Message;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.MessageType;
import com.privateboat.forum.backend.interceptor.JWTInterceptor;
import com.privateboat.forum.backend.service.ChatService;
import com.privateboat.forum.backend.util.OffsetBasedPageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerUnitTest {

    static private final Long USER_ID = 1L;
    static private final Long CHATTER_ID = 2L;
    static private final Integer UNREAD_COUNT = 12;
    static private final String TEXT_MESSAGE_CONTENT = "Hi";
    static private final Integer PAGE_OFFSET = 5;
    static private final Integer PAGE_SIZE = 10;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectionFactory projectionFactory;

    @MockBean
    private JWTInterceptor jwtInterceptor;

    @MockBean
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void sendTextMessage() throws Exception {
        TextMessageDTO textMessageDTO = new TextMessageDTO();
        Timestamp time = new Timestamp(System.currentTimeMillis());

        given(chatService.sendTextMessage(USER_ID, textMessageDTO)).willReturn(time);
        mvc.perform(MockMvcRequestBuilders.post("/chat/text")
                .requestAttr("userId", USER_ID)
                .content(objectMapper.writeValueAsString(textMessageDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(time)));
    }

    @Test
    void sendImageMessage() throws Exception {
        ImageMessageDTO imageMessageDTO = new ImageMessageDTO();
        Timestamp time = new Timestamp(System.currentTimeMillis());

        given(chatService.sendImageMessage(USER_ID, imageMessageDTO)).willReturn(time);
        mvc.perform(MockMvcRequestBuilders.post("/chat/image")
                .requestAttr("userId", USER_ID)
                .content(objectMapper.writeValueAsString(imageMessageDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(time)));
    }

    @Test
    void getTotalUnreadCount() throws Exception {
        given(chatService.getTotalUnreadCount(USER_ID)).willReturn(UNREAD_COUNT);
        mvc.perform(MockMvcRequestBuilders.get("/unread-chats")
                .requestAttr("userId", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(UNREAD_COUNT.toString()));
    }

    @Test
    void getChatHistory() throws Exception {
        Pageable pageable = new OffsetBasedPageRequest(PAGE_OFFSET, PAGE_SIZE);
        UserInfo senderInfo = new UserInfo();
        UserInfo receiverInfo = new UserInfo();
        List<Message> messages = new ArrayList<>();
        for (long i = 1; i <= 20; ++i)
            messages.add(new Message(i, senderInfo, receiverInfo,
                    new Timestamp(System.currentTimeMillis()),
                    MessageType.TEXT, TEXT_MESSAGE_CONTENT));
        Page<MessageDTO> messageDTOs =  new PageImpl<>(messages, pageable, messages.size()).map(message ->
                new MessageDTO(
                        projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, message.getSender()),
                        projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, message.getReceiver()),
                        message.getTime(), message.getType(), message.getContent()
                )
        );

        given(chatService.getChatHistory(USER_ID, CHATTER_ID, PAGE_OFFSET, PAGE_SIZE)).willReturn(messageDTOs);
        mvc.perform(MockMvcRequestBuilders.get("/chat-history")
                .requestAttr("userId", USER_ID)
                .param("chatterId", CHATTER_ID.toString())
                .param("offset", PAGE_OFFSET.toString())
                .param("limit", PAGE_SIZE.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(messageDTOs)));
    }

    @Test
    void getRecentChats() throws Exception {
        UserInfo.MinimalUserInfo minimalUserInfo =
                projectionFactory.createProjection(UserInfo.MinimalUserInfo.class, new UserInfo());
        ChatDTO chatDTO = new ChatDTO(
                minimalUserInfo, TEXT_MESSAGE_CONTENT,
                new Timestamp(System.currentTimeMillis()), UNREAD_COUNT);
        List<ChatDTO> chatDTOs = new ArrayList<>();
        for (int i = 0; i < 10; ++i)
            chatDTOs.add(chatDTO);

        given(chatService.getRecentChats(USER_ID)).willReturn(chatDTOs);
        mvc.perform(MockMvcRequestBuilders.get("/recent-chats")
                .requestAttr("userId", USER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(chatDTOs)));
    }
}