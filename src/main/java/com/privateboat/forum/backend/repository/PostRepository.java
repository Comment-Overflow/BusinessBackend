package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostRepository {
    Optional<Post> findByPostId(Long postId);
    Page<Post> findByUserId(Long userId, Pageable pageable);
}
