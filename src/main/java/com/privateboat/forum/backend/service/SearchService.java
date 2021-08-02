package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.response.SearchedCommentDTO;
import com.privateboat.forum.backend.dto.response.UserCardInfoDTO;
import com.privateboat.forum.backend.enumerate.PostTag;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchService {
    List<SearchedCommentDTO> searchComments(String searchKey, Pageable pageable);

    List<SearchedCommentDTO> searchCommentsByPostTag(PostTag postTag, String searchKey, Pageable pageable);

    void addSearchHistory(Long userId, String searchKey, PostTag postTag);

    List<UserCardInfoDTO> searchUsers(Long userId, String searchKey);
}
