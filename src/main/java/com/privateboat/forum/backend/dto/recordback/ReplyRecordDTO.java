package com.privateboat.forum.backend.dto.recordback;

import lombok.Data;

@Data
public class ReplyRecordDTO {
    private Long fromUserUserId;
    private String fromUserUserName;
    private String fromUserAvatarUrl;

    private Long timestamp;

    private String postTitle;
    private String postHostCommentContent;
    private String commentContent;
}
