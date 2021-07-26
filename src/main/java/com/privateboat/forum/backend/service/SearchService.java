package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.response.UserCardInfoDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SearchService {
    Page<Comment> searchComments(String searchKey, Pageable pageable);

    Page<Comment> searchCommentsByPostTag(PostTag postTag, String searchKey, Pageable pageable);

    List<Post> wrappedSearchedCommentsWithPost(Long userId, List<Comment> comments);

    void addSearchHistory(Long userId, String searchKey, PostTag postTag);

    List<UserCardInfoDTO> searchUsers(Long userId, String searchKey);
}
