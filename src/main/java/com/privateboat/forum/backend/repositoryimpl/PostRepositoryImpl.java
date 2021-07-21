package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.PostDAO;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostDAO postDAO;

    @Override
    public Optional<Post> findByPostId(Long postId) {
        Optional<Post> post = postDAO.findById(postId);
        post.ifPresent(Post::setTransientProperties);
        return post;
    }

    @Override
    public Page<Post> findByUserId(Long userId, Pageable pageable) {
        Page<Post> posts = postDAO.findByUserInfo_IdOrderByPostTimeDesc(userId, pageable);
        for (Post post : posts.toList()) {
            post.setTransientProperties();
        }
        return posts;
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        Page<Post> posts = postDAO.findByOrderByPostTimeDesc(pageable);
        for (Post post : posts.toList()) {
            post.setTransientProperties();
        }
        return posts;
    }

    @Override
    public Page<Post> findByTag(PostTag tag, Pageable pageable) {
        Page<Post> posts = postDAO.findByTagOrderByPostTimeDesc(tag, pageable);
        for (Post post : posts.toList()) {
            post.setTransientProperties();
        }
        return posts;
    }

    @Override
    public Post save(Post post) {
        return postDAO.save(post);
    }
}
