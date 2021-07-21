package com.privateboat.forum.backend.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ApprovalNotificationDTO {
    private Timestamp timestamp;

    // UserInfo transfer
    private String userName;
    private String avatarUrl;
    // quote transfer
//    private String title;
//    private String content;
}
