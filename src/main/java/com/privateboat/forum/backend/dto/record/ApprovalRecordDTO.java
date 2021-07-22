package com.privateboat.forum.backend.dto.record;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ApprovalRecordDTO {
    private Timestamp timestamp;

    // UserInfo transfer
    private String username;
    private String avatarUrl;
    // quote transfer
    private String title;
    private String content;
}
