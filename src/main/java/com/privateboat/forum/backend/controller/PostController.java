package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.request.NewCommentDTO;
import com.privateboat.forum.backend.dto.request.NewPostDTO;
import com.privateboat.forum.backend.dto.request.PostListDTO;
import com.privateboat.forum.backend.dto.response.PageDTO;
import com.privateboat.forum.backend.dto.response.PostContentDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.service.PostService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping(value = "/posts")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<PageDTO<Post>> getPosts(PostListDTO postListDTO) {
        try {
            Page<Post> posts;
            if (postListDTO.getTag() == null) {
                posts = postService.findAll(postListDTO.getPageNum(), postListDTO.getPageSize());
            } else {
                posts = postService.findByTag(postListDTO.getTag(), postListDTO.getPageNum(), postListDTO.getPageSize());
            }
            return ResponseEntity.ok(new PageDTO<>(posts));
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping(value = "/post")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<Long> postPost(NewPostDTO newPostDTO,
                                  @RequestAttribute Long userId) {
        try {
            Post post = postService.postPost(userId, newPostDTO);
            return ResponseEntity.ok(post.getId());
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping(value = "/comment")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<Long> postComment(@RequestBody NewCommentDTO newCommentDTO,
                                     @RequestAttribute Long userId) {
        try {
            Comment comment = postService.postComment(userId, newCommentDTO);
            return ResponseEntity.ok(comment.getId());
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping(value = "/post")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<PostContentDTO> getPost(@RequestParam Long postId,
                                           @RequestAttribute Long userId) {
        try {
            Post post = postService.getPost(postId, userId);
            return ResponseEntity.ok(new PostContentDTO(post));
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
