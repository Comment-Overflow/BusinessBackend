package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.enumerate.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentDAO extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where " +
            "(c.post.title like concat('%', :searchKey, '%') or c.content like concat('%', :searchKey, '%')) and " +
            "c.post.isDeleted = :isDeleted " +
            "order by c.time desc")
    Page<Comment> findByContentContainingOrPostTitleContainingAndPostIsDeleted(
            @Param("searchKey") String searchKey,
            @Param("isDeleted") Boolean isDeleted,
            Pageable pageable);

    @Query("select c from Comment c where " +
            "c.post.tag = :postTag and " +
            "(c.post.title like concat('%', :searchKey, '%') or c.content like concat('%', :searchKey, '%')) and " +
            "c.post.isDeleted = :isDeleted " +
            "order by c.time desc")
    Page<Comment> findByPostTagAndContentContainingOrPostTitleContainingAndPostIsDeleted(
            @Param("postTag") PostTag postTag,
            @Param("searchKey") String searchKey,
            @Param("isDeleted") Boolean isDeleted,
            Pageable pageable);

    Page<Comment> findByPostIdAndIsDeleted(Long postId, Boolean isDeleted, Pageable pageable);
}
