package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.response.SearchedCommentDTO;
import com.privateboat.forum.backend.dto.response.UserCardInfoDTO;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.service.SearchService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping(value = "/comments")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.BOTH)
    ResponseEntity<List<SearchedCommentDTO>> searchComments(
            @RequestAttribute Long userId,
            @RequestParam @Nullable PostTag postTag,
            @RequestParam String searchKey,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {

        List<SearchedCommentDTO> comments;
        if (postTag == null) {
            comments = searchService.searchComments(searchKey,
                    PageRequest.of(pageNum, pageSize));
        } else {
            comments = searchService.searchCommentsByPostTag(postTag, searchKey,
                    PageRequest.of(pageNum, pageSize));
        }
        return ResponseEntity.ok(comments);
    }

    @GetMapping(value = "/comments/followed-users")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.BOTH)
    ResponseEntity<List<SearchedCommentDTO>> getFollowingComments(
            @RequestAttribute Long userId,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        return ResponseEntity.ok(searchService.searchCommentsByFollowingUsers(
                userId, PageRequest.of(pageNum, pageSize)
        ));
    }

    @GetMapping(value = "/users")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.BOTH)
    ResponseEntity<List<UserCardInfoDTO>> searchUsers(
            @RequestAttribute Long userId,
            @RequestParam String searchKey) {
        return ResponseEntity.ok(searchService.searchUsers(userId, searchKey));
    }
}
