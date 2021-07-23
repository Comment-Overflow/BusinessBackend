package com.privateboat.forum.backend.dto.response;

import lombok.Data;

@Data
public class StarRecordDTO {
    private Long timestamp;

    private Long fromUserUserId;
    private String fromUserUserName;
    private String fromUserAvatarUrl;

    private String postTitle;
    private String postHostCommentContent;
}
