package com.privateboat.forum.backend.dto.response;

import lombok.Data;

@Data
public class ReplyRecordDTO {
    private Long fromUserUserId;
    private String fromUserUserName;
    private String fromUserAvatarUrl;

    private Long timestamp;

    private String postTitle;
    private Integer postHostCommentId;
    private String postHostCommentContent;
    private String commentContent;
    private String postHostCommentFloor;
}
