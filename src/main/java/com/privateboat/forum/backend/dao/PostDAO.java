package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostDAO extends JpaRepository<Post, Long> {
    Page<Post> findByUserInfo_IdAndIsDeletedOrderByPostTimeDesc(Long userId, Boolean isDeleted, Pageable pageable);
    Page<Post> findByIsDeletedOrderByLastCommentTimeDesc(boolean b, Pageable pageable);
    Page<Post> findByTagAndIsDeletedOrderByLastCommentTimeDesc(PostTag tag, boolean b, Pageable pageable);
}
