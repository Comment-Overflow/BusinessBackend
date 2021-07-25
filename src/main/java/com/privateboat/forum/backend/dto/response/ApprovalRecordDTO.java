package com.privateboat.forum.backend.dto.response;

import lombok.Data;

@Data
public class ApprovalRecordDTO {
    private Long timestamp;

    // UserInfo transfer
    private Long fromUserUserId;
    private String fromUserUserName;
    private String fromUserAvatarUrl;
    // comment transfer
    private Long commentId;
    private String commentPostTitle;
    private String commentContent;
    private Integer commentFloor;
}
