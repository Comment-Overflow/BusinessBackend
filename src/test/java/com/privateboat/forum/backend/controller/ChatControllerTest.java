package com.privateboat.forum.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateboat.forum.backend.interceptor.JWTInterceptor;
import com.privateboat.forum.backend.service.ChatService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JWTInterceptor jwtInterceptor;

    @MockBean
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void sendTextMessage() throws Exception {
//        TextMessageDTO textMessageDTO = new TextMessageDTO();
//        textMessageDTO.setReceiverId(2L);
//        textMessageDTO.setContent("Cool!");
//        Timestamp time = new Timestamp(System.currentTimeMillis());
//
//        given(this.chatService.sendTextMessage(1L, textMessageDTO)).willReturn(time);
//        mvc.perform(MockMvcRequestBuilders.post("/chat/text")
//                .content(objectMapper.writeValueAsString(textMessageDTO))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .requestAttr("userId", 1L))
//                .andExpect(status().isOk())
//                .andExpect(content().string(time.toString()));
    }

    @Test
    void sendImageMessage() {
    }

    @Test
    void getTotalUnreadCount() throws Exception {
        given(this.chatService.getTotalUnreadCount(1L)).willReturn(12);
        mvc.perform(MockMvcRequestBuilders.get("/unread-chats").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("12"));
    }

    @Test
    void updateChat() {
    }

    @Test
    void deleteReadChats() {
    }

    @Test
    void testDeleteReadChats() {
    }

    @Test
    void getChatHistory() {
    }

    @Test
    void getRecentChats() {
    }
}