package com.privateboat.forum.backend.dto.response;

import com.privateboat.forum.backend.entity.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class ChatDTO {

    private UserInfo.MinimalUserInfo minimalChatterInfo;
    private String lastMessageContent;
    private Timestamp time;
    private Integer unreadCount;

}
