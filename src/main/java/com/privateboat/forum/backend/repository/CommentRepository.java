package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.dto.QuoteDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.exception.PostException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CommentRepository {
    Page<Comment> findByPostId(Long postId, Pageable pageable);
    Comment save(Comment comment);
    Optional<Comment> findById(Long commentId);
    Comment getById(Long commentId) throws PostException;
    QuoteDTO getCommentAsQuote(Long commentId) throws PostException;
    void delete(Comment comment);
}
