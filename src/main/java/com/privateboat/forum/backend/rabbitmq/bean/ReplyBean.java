package com.privateboat.forum.backend.rabbitmq.bean;

import com.privateboat.forum.backend.dto.request.ReplyRecordReceiveDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReplyBean {
    private Long userId;
    private ReplyRecordReceiveDTO dto;
}
