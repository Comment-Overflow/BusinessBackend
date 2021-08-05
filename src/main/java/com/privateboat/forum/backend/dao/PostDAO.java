package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface PostDAO extends JpaRepository<Post, Long> {
    Page<Post> findByUserInfo_IdAndIsDeletedOrderByPostTimeDesc(Long userId, Boolean isDeleted, Pageable pageable);
    Page<Post> findByIsDeletedOrderByPostTimeDesc(Boolean isDeleted, Pageable pageable);
    Page<Post> findByTagAndIsDeletedOrderByPostTimeDesc(PostTag tag, Boolean isDeleted, Pageable pageable);
    List<Post> findAllByPostTimeAfter(Timestamp postTime);

    @Query(value = "select post from Post post, FollowRecord record " +
            "where record.fromUser.id = ?1 and record.toUserId = post.userInfo.id and post.isDeleted = false " +
            "order by post.postTime desc")
    Page<Post> findByFollowing(Long userId, Pageable pageable);
    Page<Post> findByIsDeletedOrderByLastCommentTimeDesc(boolean b, Pageable pageable);
    Page<Post> findByTagAndIsDeletedOrderByLastCommentTimeDesc(PostTag tag, boolean b, Pageable pageable);
}
