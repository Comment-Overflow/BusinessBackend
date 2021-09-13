package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.enumerate.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentDAO extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = "userInfo")
    Page<Comment> findByPostId(Long postId, Pageable pageable);

    @Query("select c from Comment c where " +
            "lower(c.content) like lower(concat('%', :searchKey, '%')) and c.floor > 0 and " +
            "c.post.isDeleted = :isDeleted " +
            "order by c.time desc")
    Page<Comment> findByContentContainingAndPostIsDeleted(
            @Param("searchKey") String searchKey,
            @Param("isDeleted") Boolean isDeleted,
            Pageable pageable);

    @Query("select c from Comment c where " +
            "c.post.tag = :postTag and " +
            "lower(c.content) like lower(concat('%', :searchKey, '%')) and c.floor > 0 and " +
            "c.post.isDeleted = :isDeleted " +
            "order by c.time desc")
    Page<Comment> findByPostTagAndContentContainingAndPostIsDeleted(
            @Param("postTag") PostTag postTag,
            @Param("searchKey") String searchKey,
            @Param("isDeleted") Boolean isDeleted,
            Pageable pageable);

    @Query(value = "select c from Comment c, FollowRecord record " +
            "where record.fromUser.id = ?1 and record.toUserId = c.userInfo.id and c.post.isDeleted = false " +
            "order by c.time desc")
    Page<Comment> findByFollowingOnly(Long userId, Pageable pageable);

    Page<Comment> findByPostIdAndIsDeleted(Long postId, Boolean isDeleted, Pageable pageable);

    Page<Comment> findByUserInfoIdAndFloorGreaterThanAndIsDeletedOrderByTimeDesc(Long userId, Integer floor, Boolean isDeleted, Pageable pageable);

    @Modifying
    @Query(value = "update comment set is_deleted = false where post_id = ?1", nativeQuery = true)
    void deleteCommentsByPostId(Long postId);
}
