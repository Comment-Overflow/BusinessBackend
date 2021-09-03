package com.privateboat.forum.backend.rabbitmq.bean;

import com.privateboat.forum.backend.enumerate.StatisticType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticBean {
    private Long userId;
    private StatisticType type;
}
