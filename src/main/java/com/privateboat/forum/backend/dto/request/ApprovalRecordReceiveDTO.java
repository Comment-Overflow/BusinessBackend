package com.privateboat.forum.backend.dto.request;

import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalRecordReceiveDTO {
    Long commentId;
    Long toUserId;
    ApprovalStatus status;
}
