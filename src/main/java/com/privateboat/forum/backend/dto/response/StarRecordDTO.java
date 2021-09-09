package com.privateboat.forum.backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StarRecordDTO {
    private Long timestamp;

    private Long fromUserUserId;
    private String fromUserUserName;
    private String fromUserAvatarUrl;

    private String postTitle;
    private String postHostCommentContent;
    private Long postHostCommentId;
    private Integer postHostCommentFloor;
}
