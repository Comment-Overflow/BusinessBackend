package com.privateboat.forum.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReplyRecordReceiveDTO {
    private Long toUserId;
    private Long postId;
    private Long commentId;
    private Long quoteCommentId;
}
