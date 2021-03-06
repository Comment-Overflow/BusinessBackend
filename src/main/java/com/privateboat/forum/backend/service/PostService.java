package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.request.NewCommentDTO;
import com.privateboat.forum.backend.dto.request.NewPostDTO;
import com.privateboat.forum.backend.dto.response.PageDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.enumerate.SortPolicy;
import com.privateboat.forum.backend.exception.PostException;
import org.springframework.data.domain.Page;

public interface PostService {
    Page<Post> findByTag(PostTag tag, Integer pageNum, Integer pageSize, Long userId) throws PostException;
    Page<Post> findAll(Integer pageNum, Integer pageSize, Long userId) throws PostException;
    Page<Post> findOnesPosts(Long userId, Integer pageNum, Integer pageSize, Long myUserId);
    Page<Post> findStarredPosts(Long userId, Integer pageNum, Integer pageSize);
    Page<Post> findFollowingOnly(Integer pageNum, Integer pageSize, Long userId);
    Post postPost(Long userId, NewPostDTO newPostDTO) throws PostException;
    Comment postComment(Long userId, NewCommentDTO commentDTO) throws PostException;
    Post getPost(Long postId, Long userId) throws PostException;
    Post getPostByComment(Long commentId, Long userId) throws PostException;
    PageDTO<Comment> findByPostIdOrderByPolicy(Long postId, SortPolicy policy,
                                               Integer pageNum, Integer pageSize, Long userId);
    void deletePost(Long postId, Long userId) throws PostException;
    void deleteComment(Long commentId, Long userId) throws PostException;
    void setPostTransientField(Post post, UserInfo userInfo);
}
