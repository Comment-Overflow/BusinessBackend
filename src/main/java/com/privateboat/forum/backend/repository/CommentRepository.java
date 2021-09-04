package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.dto.QuoteDTO;
import com.privateboat.forum.backend.dto.response.PageDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.exception.PostException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CommentRepository {
    PageDTO<Comment> findByPostId(Long postId, Pageable pageable);
    Comment save(Comment comment);
    Comment saveAndFlush(Comment newComment);
    Optional<Comment> findById(Long commentId);
    Comment getById(Long commentId) throws PostException;
    Page<Comment> findByContentContainingAndIsDeleted(String searchKey, Boolean isDeleted, Pageable pageable);
    Page<Comment> findByPostTag(PostTag tag, String searchKey, Pageable pageable);
    Page<Comment> findByFollowingOnly(Long userId, Pageable pageable);
    Page<Comment> getOnesComments(Long userId, Pageable pageable);
    QuoteDTO getCommentAsQuote(Long commentId) throws PostException;
    void setIsDeletedAndFlush(Comment comment);
    PageDTO<Comment> updateCommentCache(Long postId, Pageable pageable);
    boolean existsById(Long commentId);
    void deleteCommentsByPostId(Long postId);
}
