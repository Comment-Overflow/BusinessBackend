package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.response.PageDTO;
import com.privateboat.forum.backend.dto.response.SearchedCommentDTO;
import com.privateboat.forum.backend.entity.Comment;
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

@RestController
@AllArgsConstructor
public class CommentController {

    private final SearchService searchService;

    @GetMapping(value = "/comments")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.BOTH)
    ResponseEntity<PageDTO<SearchedCommentDTO>> searchComments(
            @RequestAttribute Long userId,
            @RequestParam @Nullable PostTag postTag,
            @RequestParam String searchKey,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {

        Page<Comment> comments;
        if (postTag == null) {
            comments = searchService.searchComments(searchKey,
                    PageRequest.of(pageNum, pageSize));
        } else {
            comments = searchService.searchCommentsByPostTag(postTag, searchKey,
                    PageRequest.of(pageNum, pageSize));
        }

        // Record the time and search key of this search.
        searchService.addSearchHistory(userId, searchKey);
        
        // Convert searched comments in to DTO.
        Page<SearchedCommentDTO> searchedComments = comments.map(
                comment -> new SearchedCommentDTO(comment.getPost().getId(), comment.getPost().getTitle(), comment)
        );
        return ResponseEntity.ok(new PageDTO<>(searchedComments));
    }
}
