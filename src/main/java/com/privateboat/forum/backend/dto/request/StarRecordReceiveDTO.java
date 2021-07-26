package com.privateboat.forum.backend.dto.request;

import lombok.Data;

@Data
public class StarRecordReceiveDTO {
    private Long toUserId;
    private Long postId;
}
