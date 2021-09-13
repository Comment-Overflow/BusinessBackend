package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.enumerate.PreferenceDegree;

import java.util.List;

public interface PreferencePostRepository {
    void addPreferencePostRecord(Long userId, Long postId, PreferenceDegree preferenceDegree);
    void clearExpiredRecord();
    List<Long> getOneUserPreferredPostId(Long userId);
}
