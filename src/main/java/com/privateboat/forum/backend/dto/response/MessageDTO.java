package com.privateboat.forum.backend.dto.response;

import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class MessageDTO {

    private UserInfo.MinimalUserInfo minimalSenderInfo;
    private UserInfo.MinimalUserInfo minimalReceiverInfo;
    private Timestamp time;
    private MessageType type;
    private String content;

}
