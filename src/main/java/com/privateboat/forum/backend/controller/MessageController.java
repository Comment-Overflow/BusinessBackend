package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.request.ChatHistoryDTO;
import com.privateboat.forum.backend.dto.request.TextMessageDTO;
import com.privateboat.forum.backend.entity.Message;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.service.MessageService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

//    @PostMapping(value = "/message/text")
//    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
//    public ResponseEntity<?> sendTextMessage(@RequestBody TextMessageDTO textMessageDTO) {
//        try {
//            messageService.sendTextMessage(textMessageDTO.getContent(), textMessageDTO.getSenderId(), textMessageDTO.getReceiverId());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
//
//    @GetMapping(value = "/messages")
//    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
//    public ResponseEntity<?> getChatHistory(@RequestParam Long userId, @RequestParam Long chatterId) {
//        try {
//
//        }
//    }

    @MessageMapping("/chat/text")
    public void sendTextMessage(
            @Payload TextMessageDTO textMessageDTO,
            @Header("Authorization") String token,
            SimpMessageHeaderAccessor ha
    ) {
//        Map<String, Object> map = ha.getSessionAttributes();
//        if (map != null)
//            for (String key : map.keySet()) {
//                System.out.println(key);
//                System.out.println(map.get(key).toString());
//            }
//        String ip = ha.getSessionAttributes().get("ip").toString();
//        System.out.println(ip);
        messageService.sendTextMessage(textMessageDTO.getUuid(), textMessageDTO.getSenderId(), textMessageDTO.getReceiverId(), textMessageDTO.getContent());
    }

    @MessageMapping("/chat/image")
    public void getImageMessage(
            @Payload byte[] imgByteArray,
            @Header("Authorization") String token,
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

    @GetMapping("/chat-history")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    public ResponseEntity<?> getChatHistory(@RequestBody ChatHistoryDTO chatHistoryDTO) {
        Page<Message> response;
        try {
            response = messageService.getChatHistory(chatHistoryDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
