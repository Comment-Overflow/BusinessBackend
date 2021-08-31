package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.KeyWord;

import java.util.List;

public interface KeyWordRepository {
    List<KeyWord> getKeyWordByPostId(Long postId);
    void saveNewPostKeyWord(List<KeyWord> keyWordList);
}
