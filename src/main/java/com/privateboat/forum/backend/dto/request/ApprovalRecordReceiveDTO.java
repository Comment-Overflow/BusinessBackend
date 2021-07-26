package com.privateboat.forum.backend.dto.request;

import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import lombok.Data;

@Data
public class ApprovalRecordReceiveDTO {
    private Long commentId;
    private Long toUserId;
    private ApprovalStatus status;
}
