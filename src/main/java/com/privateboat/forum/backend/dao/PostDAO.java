package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostDAO extends JpaRepository<Post, Long> {
    Page<Post> findByUserInfo_IdOrderByPostTimeDesc(Long userId, Pageable pageable);
    Page<Post> findByOrderByPostTimeDesc(Pageable pageable);
    Page<Post> findByTagOrderByPostTimeDesc(PostTag tag, Pageable pageable);
}
