package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.response.SearchedCommentDTO;
import com.privateboat.forum.backend.dto.response.UserCardInfoDTO;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.exception.UserInfoException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchService {
    List<SearchedCommentDTO> searchAllComments(Long userId, String searchKey, Pageable pageable)
            throws UserInfoException;

    List<SearchedCommentDTO> searchCommentsByPostTag(Long userId, PostTag postTag, String searchKey, Pageable pageable)
            throws UserInfoException;

    List<SearchedCommentDTO> searchCommentsByFollowingUsers(Long userId, Pageable pageable);

    List<UserCardInfoDTO> searchUsers(Long userId, String searchKey);
}
