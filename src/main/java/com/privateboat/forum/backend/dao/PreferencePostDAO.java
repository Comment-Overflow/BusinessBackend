package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.PreferencePost;
import com.privateboat.forum.backend.entity.PreferencePostID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferencePostDAO extends JpaRepository<PreferencePost, PreferencePostID> {
    Optional<PreferencePost> findByUserIdAndPostId(Long userId, Long postId);
}
