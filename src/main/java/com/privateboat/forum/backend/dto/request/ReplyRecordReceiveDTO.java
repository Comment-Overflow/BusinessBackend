package com.privateboat.forum.backend.dto.request;

import lombok.Data;

@Data
public class ReplyRecordReceiveDTO {
    private Long toUserId;
    private Long postId;
    private int floor;
}
