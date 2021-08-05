package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostDAO extends JpaRepository<Post, Long> {
    Page<Post> findByUserInfo_IdAndIsDeletedOrderByPostTimeDesc(Long userId, Boolean isDeleted, Pageable pageable);
    Page<Post> findByIsDeletedOrderByPostTimeDesc(Boolean isDeleted, Pageable pageable);
    Page<Post> findByTagAndIsDeletedOrderByPostTimeDesc(PostTag tag, Boolean isDeleted, Pageable pageable);
}
