package com.privateboat.forum.backend.scheduluedtask;

import com.privateboat.forum.backend.util.RedisUtil;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "com.privateboat.forum.backend", name = "update-record-counter-rate")
@AllArgsConstructor
public class RecordUpdateTask {
    private final RedisUtil redisUtil;

    @Scheduled(fixedRateString = "${com.privateboat.forum.backend.update-record-counter-rate}")
    void update() {
        redisUtil.dailyRecordUpdate();
    }
}
