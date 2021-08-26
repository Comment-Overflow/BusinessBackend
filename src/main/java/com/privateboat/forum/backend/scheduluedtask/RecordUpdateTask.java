package com.privateboat.forum.backend.scheduluedtask;

import com.privateboat.forum.backend.util.RedisUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "com.privateboat.forum.backend", name = "update-record-counter-rate")
@AllArgsConstructor
public class RecordUpdateTask {
    private final RedisUtil redisUtil;

    @Scheduled(cron = "0 0 0 * * *")
    void update() {
        log.info("Updating daily statistic counter...");
        redisUtil.dailyRecordUpdate();
        log.info("Daily statistic counter updated.");
    }
}
