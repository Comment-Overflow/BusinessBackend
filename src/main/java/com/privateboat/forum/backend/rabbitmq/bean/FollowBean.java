package com.privateboat.forum.backend.rabbitmq.bean;

import com.privateboat.forum.backend.enumerate.MQMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FollowBean {
    private Long fromUserId;
    private Long toUserId;
    private MQMethod method;
}
