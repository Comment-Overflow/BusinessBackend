package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.PreferredWord;
import com.privateboat.forum.backend.enumerate.PostTag;

import java.util.HashMap;

public interface PreferredWordRepository {
    HashMap<PostTag, HashMap<String, Long>> findAllByUserId(Long userId);

    HashMap<String, PreferredWord.wordWithId> findAllByUserIdAndPostTag(Long userId, PostTag postTag);

    void addPreferredWord(PreferredWord preferredWord);

    void updatePreferredWord(Long wordId, Long score);
}