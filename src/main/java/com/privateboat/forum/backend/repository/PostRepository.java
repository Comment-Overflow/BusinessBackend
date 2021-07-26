package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.exception.PostException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostRepository {
    Optional<Post> findByPostId(Long postId);
    Page<Post> findByUserId(Long userId, Pageable pageable);
    Page<Post> findAll(Pageable pageable);
    Page<Post> findByTag(PostTag tag, Pageable pageable);
    Post save(Post post);
    Post getByPostId(Long postId) throws PostException;
    void delete(Post post);
}
