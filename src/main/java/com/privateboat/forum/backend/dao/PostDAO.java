package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostDAO extends JpaRepository<Post, Long> {
    Page<Post> findByUserInfo_Id(Long userId, Pageable pageable);
}
