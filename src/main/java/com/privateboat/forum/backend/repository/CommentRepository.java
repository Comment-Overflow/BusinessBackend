package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepository {
    Page<Comment> findByPostId(Long postId, Pageable pageable);
    Comment save(Comment comment);
}
