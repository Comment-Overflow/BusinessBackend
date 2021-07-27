package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.enumerate.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentDAO extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostId(Long postId, Pageable pageable);

    @Query("select c from Comment c where " +
            "c.post.title like concat('%', :searchKey, '%') or c.content like concat('%', :searchKey, '%') " +
            "order by c.time desc")
    Page<Comment> findByContentContainingOrPostTitleContaining(
            @Param("searchKey") String searchKey,
            Pageable pageable);

    @Query("select c from Comment c where " +
            "c.post.tag = :postTag and " +
            "(c.post.title like concat('%', :searchKey, '%') or c.content like concat('%', :searchKey, '%')) " +
            "order by c.time desc")
    Page<Comment> findByPostTagAndContentContainingOrPostTitleContaining(
            @Param("postTag") PostTag postTag,
            @Param("searchKey") String searchKey,
            Pageable pageable);

    Page<Comment> findByPostIdAndIsDeleted(Long postId, Boolean isDeleted, Pageable pageable);
}
