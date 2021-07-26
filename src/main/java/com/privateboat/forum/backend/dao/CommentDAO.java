package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentDAO extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostIdAndIsDeleted(Long postId, Boolean isDeleted, Pageable pageable);
}
