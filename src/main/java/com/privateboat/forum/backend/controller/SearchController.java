package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.response.UserCardInfoDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.service.SearchService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
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
    ResponseEntity<List<Post>> searchComments(
            @RequestAttribute Long userId,
            @RequestParam @Nullable String postTagStr,
            @RequestParam String searchKey,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {

        PostTag postTag = postTagStr == null ? null : PostTag.toPostTag(postTagStr);
        Page<Comment> commentsPage;
        if (postTag == null) {
            commentsPage = searchService.searchComments(searchKey,
                    PageRequest.of(pageNum, pageSize));
        } else {
            commentsPage = searchService.searchCommentsByPostTag(postTag, searchKey,
                    PageRequest.of(pageNum, pageSize));
        }

        // Record the time and search key of this search.
        searchService.addSearchHistory(userId, searchKey, postTag);

        // Get the content of the page.
        List<Comment> commentsList = commentsPage.getContent();
        List<Post> postsContainingTheComment = searchService.wrappedSearchedCommentsWithPost(userId, commentsList);

        return ResponseEntity.ok(postsContainingTheComment);
    }

    @GetMapping(value = "/users")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.BOTH)
    ResponseEntity<List<UserCardInfoDTO>> searchUsers(
            @RequestAttribute Long userId,
            @RequestParam String searchKey) {
        return ResponseEntity.ok(searchService.searchUsers(userId, searchKey));
    }
}
