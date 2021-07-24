package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.enumerate.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {
    Page<Comment> searchComments(String searchKey, Pageable pageable);

    Page<Comment> searchCommentsByPostTag(PostTag postTag, String searchKey, Pageable pageable);

    void addSearchHistory(Long userId, String searchKey);
}
