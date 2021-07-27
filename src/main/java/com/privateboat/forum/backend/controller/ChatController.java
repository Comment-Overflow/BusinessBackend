package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.request.ImageMessageDTO;
import com.privateboat.forum.backend.dto.request.TextMessageDTO;
import com.privateboat.forum.backend.service.ChatService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat/text")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    public ResponseEntity<?>  sendTextMessage(TextMessageDTO textMessageDTO, @RequestAttribute Long userId) {
        try {
            return ResponseEntity.ok(chatService.sendTextMessage(userId, textMessageDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/chat/image")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    public ResponseEntity<?> sendImageMessage(ImageMessageDTO imageMessageDTO, @RequestAttribute Long userId) {
        try {
            return ResponseEntity.ok(chatService.sendImageMessage(userId, imageMessageDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/unread-chats")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    public ResponseEntity<?> getTotalUnreadCount(@RequestAttribute Long userId) {
        try {
            return ResponseEntity.ok(chatService.getTotalUnreadCount(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @MessageMapping("/chat/update")
    public void updateChat(
            @Header("UserId") Long userId,
            @Header("ChatterId") Long chatterId
    ) {
        chatService.updateSeenBy(userId, chatterId);
    }

    @MessageMapping("/user/read-chats/delete")
    public void deleteReadChats(@Header("UserId") Long userId) {
        try {
            chatService.deleteAllReadChat(userId);
        } catch (Exception e) {
            // Just ignore.
            e.printStackTrace();
        }
    }

    @MessageMapping("/user/chat/delete")
    public void deleteReadChats(@Header("UserId") Long userId, @Header("ChatterId") Long chatterId) {
        try {
            chatService.deleteChat(userId, chatterId);
        } catch (Exception e) {
            // Just ignore.
            e.printStackTrace();
        }
    }

    @GetMapping("/chat-history")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    public ResponseEntity<?> getChatHistory(
            @RequestAttribute Long userId,
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
    public ResponseEntity<?> getRecentChats(@RequestAttribute Long userId) {
        try {
            return ResponseEntity.ok(chatService.getRecentChats(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
