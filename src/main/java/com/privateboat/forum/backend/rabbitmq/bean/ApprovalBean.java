package com.privateboat.forum.backend.rabbitmq.bean;

import com.privateboat.forum.backend.dto.request.ApprovalRecordReceiveDTO;
import com.privateboat.forum.backend.enumerate.MQMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalBean {
    private Long userId;
    private ApprovalRecordReceiveDTO dto;
    private MQMethod method;
}
