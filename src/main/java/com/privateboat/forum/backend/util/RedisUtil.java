package com.privateboat.forum.backend.util;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@AllArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    static private final String viewsString = "views-";
    static private final String dayString = "day";
    static private final String weekString = "week";
    static private final String yearString = "year";
    static private final String foreverString = "forever";

    void addViewsCounter() {
        stringRedisTemplate.opsForValue().increment(viewsString + dayString);
        stringRedisTemplate.opsForValue().increment(viewsString + weekString);
        stringRedisTemplate.opsForValue().increment(viewsString + yearString);
    }
}
