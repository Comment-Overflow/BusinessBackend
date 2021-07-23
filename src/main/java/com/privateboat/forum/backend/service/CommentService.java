package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.enumerate.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    Page<Comment> searchAll(String searchKey, Pageable pageable);

    Page<Comment> searchByPostTag(PostTag postTag, String searchKey, Pageable pageable);
}
