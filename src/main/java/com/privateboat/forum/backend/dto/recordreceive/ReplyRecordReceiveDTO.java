package com.privateboat.forum.backend.dto.recordreceive;

import lombok.Data;

@Data
public class ReplyRecordReceiveDTO {
    private Long toUserId;
    private Long postId;
    private int floor;
}
