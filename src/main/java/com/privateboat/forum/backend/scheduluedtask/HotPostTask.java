package com.privateboat.forum.backend.scheduluedtask;


import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(prefix = "com.privateboat.forum.backend", name = "compute-hot-list-rate")
@AllArgsConstructor
public class HotPostTask {
    private static final Integer LIMIT = 50;

    PostRepository postRepository;

    @Scheduled(fixedRateString = "${com.privateboat.forum.backend.compute-hot-list-rate}")
    void getHostPostList() {
        List<Post> hottestPosts = postRepository.getHotPosts(LIMIT);
        // TODO: add to redis
    }
}
