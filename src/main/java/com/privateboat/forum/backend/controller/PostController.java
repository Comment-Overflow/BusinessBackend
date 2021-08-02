package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.request.NewCommentDTO;
import com.privateboat.forum.backend.dto.request.NewPostDTO;
import com.privateboat.forum.backend.dto.response.PageDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.enumerate.SortPolicy;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.service.PostService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping(value = "/posts")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<PageDTO<Post>> getPosts(PostTag tag,
                                           @RequestParam("pageNum") Integer pageNum,
                                           @RequestParam("pageSize") Integer pageSize,
                                           @RequestParam("followingOnly") Boolean followingOnly,
                                           @RequestAttribute Long userId) {
        try {
            Page<Post> posts;
            if (followingOnly) {
                posts = postService.findFollowingOnly(pageNum, pageSize, userId);
            } else if (tag == null) {
                posts = postService.findAll(pageNum, pageSize, userId);
            } else {
                posts = postService.findByTag(tag, pageNum, pageSize, userId);
            }
            return ResponseEntity.ok(new PageDTO<>(posts));
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping(value = "/post")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<Post> postPost(NewPostDTO newPostDTO,
                                  @RequestAttribute Long userId) {
        try {
            Post post = postService.postPost(userId, newPostDTO);
            post.setIsStarred(false);
            return ResponseEntity.ok(post);
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping(value = "/comment")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<Integer> postComment(NewCommentDTO newCommentDTO,
                                     @RequestAttribute Long userId) {
        try {
            Comment comment = postService.postComment(userId, newCommentDTO);
            return ResponseEntity.ok(comment.getFloor());
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping(value = "/post")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<Post> getPost(@RequestParam("postId") Long postId,
                                 @RequestAttribute Long userId) {
        try {
            return ResponseEntity.ok(postService.getPost(postId, userId));
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping(value = "/post/comments")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<PageDTO<Comment>> getPostComments(@RequestParam("postId") Long postId,
                                             @RequestParam("policy") SortPolicy policy,
                                             @RequestParam("pageNum") Integer pageNum,
                                             @RequestParam("pageSize") Integer pageSize,
                                             @RequestAttribute Long userId) {
        try {
            PageDTO<Comment> comments = postService.findByPostIdOrderByPolicy(
                    postId, policy, pageNum, pageSize, userId
            );
            return ResponseEntity.ok(comments);
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping(value = "/post")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<?> deletePost(@RequestParam("postId") Long postId,
                                 @RequestAttribute Long userId) {
        try {
            postService.deletePost(postId, userId);
            return ResponseEntity.ok().build();
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping(value = "/comment")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<?> deleteComment(@RequestParam("commentId") Long commentId,
                                    @RequestAttribute Long userId) {
        try {
            postService.deleteComment(commentId, userId);
            return ResponseEntity.ok().build();
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping(value = "/comment/post")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<Post> getPostByCommentId(@RequestParam("commentId") Long commentId,
                                            @RequestAttribute Long userId) {
        try {
            return ResponseEntity.ok(postService.getPostByComment(commentId, userId));
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


}
