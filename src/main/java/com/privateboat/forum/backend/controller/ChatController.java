package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.request.TextMessageDTO;
import com.privateboat.forum.backend.service.ChatService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/text")
    public void sendTextMessage(@Payload TextMessageDTO textMessageDTO) {
        chatService.sendTextMessage(textMessageDTO.getUuid(), textMessageDTO.getSenderId(), textMessageDTO.getReceiverId(), textMessageDTO.getContent());
    }

    @MessageMapping("/chat/image")
    public void getImageMessage(
            @Payload byte[] imgByteArray,
            @Header("UUID") String uuid
    ) {
        System.out.println("image");
        for (byte b : imgByteArray) {
            System.out.print(b);
            System.out.print(' ');
        }
        System.out.println();
        simpMessagingTemplate.convertAndSend("/notify/" + uuid, "success");
    }

    @MessageMapping("/chat/update")
    public void updateChat(
            @Header("UserId") Long userId,
            @Header("ChatterId") Long chatterId
    ) {
        chatService.updateSeenBy(userId, chatterId);
    }

    @GetMapping("/chat-history")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    public ResponseEntity<?> getChatHistory(
            @RequestParam("userId") Long userId,
            @RequestParam("chatterId") Long chatterId,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize
    ) {
        try {
            return ResponseEntity.ok(chatService.getChatHistory(userId, chatterId, pageNum, pageSize));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/recent-chats")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    public ResponseEntity<?> getRecentChats(@RequestParam("userId") Long userId) {
        try {
            return ResponseEntity.ok(chatService.getRecentChats(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
