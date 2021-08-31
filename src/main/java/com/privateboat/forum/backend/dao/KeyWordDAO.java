package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.KeyWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeyWordDAO extends JpaRepository<KeyWord, Long> {
    List<KeyWord> getKeyWordsByPostId(Long postId);
}
