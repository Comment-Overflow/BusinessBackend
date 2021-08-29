package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.enumerate.PreferenceDegree;

public interface PreferencePostRepository {
    void addPreferencePostRecord(Long userId, Long postId, PreferenceDegree preferenceDegree);
    void clearExpiredRecord();
}
