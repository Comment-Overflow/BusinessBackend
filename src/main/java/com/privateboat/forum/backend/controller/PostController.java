package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.request.PostDTO;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.service.PostService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping(value = "/post")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<Page<Post>> getTaggedPosts(PostDTO postDTO) {
        try {
            Page<Post> posts;
            if (postDTO.getTag() == null) {
                posts = postService.findAll(postDTO.getPageNum(), postDTO.getPageSize());
            } else {
                posts = postService.findByTag(postDTO.getTag(), postDTO.getPageNum(), postDTO.getPageSize());
            }
            return ResponseEntity.ok(posts);
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


}
