package com.privateboat.forum.backend.dto.recordback;

import lombok.Data;

@Data
public class ApprovalRecordDTO {
    private Long timestamp;

    // UserInfo transfer
    private Long fromUserUserId;
    private String fromUserUserName;
    private String fromUserAvatarUrl;
    // comment transfer
    private String commentPostTitle;
    private String commentContent;
}
