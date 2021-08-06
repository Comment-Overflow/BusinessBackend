package com.privateboat.forum.backend.scheduluedtask;


import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.privateboat.forum.backend.util.Constant.REDIS_HOT_LIST_KEY;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "com.privateboat.forum.backend", name = "compute-hot-list-rate")
@AllArgsConstructor
public class HotPostTask {
    private static final Integer LIMIT = 50;
    private final RedisTemplate<String, Object> redisTemplate;

    PostRepository postRepository;

    @Scheduled(fixedRateString = "${com.privateboat.forum.backend.compute-hot-list-rate}")
    void getHostPostList() {
        log.info("HotPost: generating hot posts...");
        List<Post> hottestPosts = postRepository.generateHotPosts(LIMIT);
        log.info(hottestPosts.size() + " posts found, saving to redis...");

        if (!hottestPosts.isEmpty()) {
            redisTemplate.execute(new SessionCallback<List<Object>>() {
                @Nullable
                @Override
                public List<Object> execute(@NonNull RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.delete(REDIS_HOT_LIST_KEY);
                    operations.opsForList().rightPushAll(REDIS_HOT_LIST_KEY, hottestPosts);
                    operations.exec();
                    return null;
                }
            });
        }
        log.info("All hot posts were saved.");
    }
}
