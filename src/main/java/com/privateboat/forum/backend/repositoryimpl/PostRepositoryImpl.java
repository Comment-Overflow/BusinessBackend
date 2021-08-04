package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.PostDAO;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostDAO postDAO;

    @Override
    public Optional<Post> findByPostId(Long postId) {
        return postDAO.findById(postId);
    }

    @Override
    public Page<Post> findByUserId(Long userId, Pageable pageable) {
        return postDAO.findByUserInfo_IdAndIsDeletedOrderByPostTimeDesc(userId, false, pageable);
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postDAO.findByIsDeletedOrderByPostTimeDesc(false, pageable);
    }

    @Override
    public Page<Post> findFollowingOnly(Long userId, Pageable pageable) {
        return postDAO.findByFollowing(userId, pageable);
    }

    @Override
    public Page<Post> findByTag(PostTag tag, Pageable pageable) {
        return postDAO.findByTagAndIsDeletedOrderByPostTimeDesc(tag, false, pageable);
    }

    @Override
    public Post save(Post post) {
        return postDAO.save(post);
    }

    @Override
    public Post getByPostId(Long postId) throws PostException {
        try {
            return postDAO.getById(postId);
        } catch (EntityNotFoundException e){
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }
    }

    @Override
    public void delete(Post post) {
        post.setIsDeleted(true);
        postDAO.save(post);
    }

    @Override
    public List<Post> getHotPosts(Integer limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Order.desc("hotIndex")));
        return postDAO.findAll(pageRequest).getContent();
    }
}
