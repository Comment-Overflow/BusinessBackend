package com.privateboat.forum.backend.dto.response;

import lombok.Value;

@Value
public class ReplyRecordDTO {
    Long userId;
    String userName;
    String userAvatarUrl;

    Long timestamp;

    String postTitle;
    Long quoteCommentId;
    String quoteCommentContent;
    String replyContent;
    Long replyCommentId;
    Integer replyCommentFloor;
    Integer commentFloor;
}
