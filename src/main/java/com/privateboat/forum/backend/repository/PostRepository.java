package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.exception.PostException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> findByPostId(Long postId);
    Page<Post> findByUserId(Long userId, Pageable pageable);
    Page<Post> findAll(Pageable pageable);
    List<Post.allPostIdWithTag> findAllRecentPost();
    Page<Post> findByTag(PostTag tag, Pageable pageable);
    Post save(Post post);
    Post saveAndFlush(Post post);
    Post getByPostId(Long postId) throws PostException;
    void setIsDeletedAndFlush(Post post);
    List<Post> generateHotPosts(Integer limit);
    List<Post> getHotPosts(Pageable pageable);
    Page<Post> findByTitleContainingAndIsDeletedOrderByPostTime(String searchKey, boolean b, Pageable pageable);
    Page<Post> findByTitleContainingAndTagAndIsDeletedOrderByPostTime(String searchKey, PostTag tag, boolean b, Pageable pageable);
}
