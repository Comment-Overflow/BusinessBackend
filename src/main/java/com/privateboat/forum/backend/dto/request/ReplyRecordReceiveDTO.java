package com.privateboat.forum.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyRecordReceiveDTO {
    private Long toUserId;
    private Long postId;
    private Long commentId;
    private Long quoteCommentId;
}
