package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.PostDAO;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.exception.PostException;
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
        return postDAO.findById(postId);
    }

    @Override
    public Page<Post> findByUserId(Long userId, Pageable pageable) {
        return postDAO.findByUserInfo_IdOrderByPostTimeDesc(userId, pageable);
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postDAO.findByOrderByPostTimeDesc(pageable);
    }

    @Override
    public Page<Post> findByTag(PostTag tag, Pageable pageable) {
        return postDAO.findByTagOrderByPostTimeDesc(tag, pageable);
    }

    @Override
    public Post save(Post post) {
        return postDAO.save(post);
    }

    @Override
    public Post getByPostId(Long postId) throws PostException {
        return postDAO.getById(postId);
    }
}
