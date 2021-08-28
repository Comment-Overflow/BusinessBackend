package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface PostDAO extends JpaRepository<Post, Long> {
    Page<Post> findByUserInfo_IdAndIsDeletedOrderByPostTimeDesc(Long userId, Boolean isDeleted, Pageable pageable);
    List<Post> findAllByPostTimeAfter(Timestamp postTime);
    Page<Post> findByIsDeletedOrderByLastCommentTimeDesc(boolean b, Pageable pageable);
    Page<Post> findByTagAndIsDeletedOrderByLastCommentTimeDesc(PostTag tag, boolean b, Pageable pageable);

    @Query("select p from Post p where " +
            "lower(p.title) like lower(concat('%', :searchKey, '%')) and " +
            "p.isDeleted = :isDeleted " +
            "order by p.postTime desc")
    Page<Post> findByTitleContainingAndIsDeletedOrderByPostTime(
            @Param("searchKey") String searchKey,
            @Param("isDeleted") boolean isDeleted,
            Pageable pageable);

    @Query("select p from Post p where " +
            "lower(p.title) like lower(concat('%', :searchKey, '%')) and " +
            "p.tag = :tag and " +
            "p.isDeleted = :isDeleted " +
            "order by p.postTime desc")
    Page<Post> findByTitleContainingAndTagAndIsDeletedOrderByPostTime(
            @Param("searchKey") String searchKey,
            @Param("tag") PostTag tag,
            @Param("isDeleted") boolean isDeleted,
            Pageable pageable);
}
