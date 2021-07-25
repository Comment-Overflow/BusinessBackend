package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.dto.QuoteDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.exception.PostException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CommentRepository {
    Page<Comment> findByPostId(Long postId, Pageable pageable);
    Comment save(Comment comment);
    Optional<Comment> findById(Long commentId);
    Comment getById(Long commentId) throws PostException;
    Page<Comment> searchAll(String searchKey, Pageable pageable);
    Page<Comment> searchByTag(PostTag tag, String searchKey, Pageable pageable);
    QuoteDTO getCommentAsQuote(Long commentId) throws PostException;
}
