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
            "(lower(c.post.title) like lower(concat('%', :searchKey, '%')) and c.floor = 0 " +
            "or lower(c.content) like lower(concat('%', :searchKey, '%'))) and " +
            "c.post.isDeleted = :isDeleted " +
            "order by c.time desc")
    Page<Comment> findByContentContainingOrPostTitleContainingAndPostIsDeleted(
            @Param("searchKey") String searchKey,
            @Param("isDeleted") Boolean isDeleted,
            Pageable pageable);

    @Query("select c from Comment c where " +
            "c.post.tag = :postTag and " +
            "(lower(c.post.title) like lower(concat('%', :searchKey, '%')) and c.floor = 0 " +
            "or lower(c.content) like lower(concat('%', :searchKey, '%'))) and " +
            "c.post.isDeleted = :isDeleted " +
            "order by c.time desc")
    Page<Comment> findByPostTagAndContentContainingOrPostTitleContainingAndPostIsDeleted(
            @Param("postTag") PostTag postTag,
            @Param("searchKey") String searchKey,
            @Param("isDeleted") Boolean isDeleted,
            Pageable pageable);

    Page<Comment> findByPostIdAndIsDeleted(Long postId, Boolean isDeleted, Pageable pageable);
}
