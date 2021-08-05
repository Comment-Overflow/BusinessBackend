package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.PreferredWord;
import com.privateboat.forum.backend.enumerate.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreferredWordDAO extends JpaRepository<PreferredWord, Long> {
    List<PreferredWord> findAllByUserId(Long userId);
    List<PreferredWord.wordWithId> findAllByUserIdAndPostTag(Long userId, PostTag postTag);
}
