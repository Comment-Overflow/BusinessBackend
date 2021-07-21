package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.repository.PostRepository;
import com.privateboat.forum.backend.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Override
    public Page<Post> findByTag(PostTag tag, Integer pageNum, Integer pageSize) throws PostException {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Post> posts = postRepository.findByTag(tag, pageable);
        if (posts.getTotalPages() < pageNum)
            throw new PostException(PostException.PostExceptionType.PAGE_OUT_OF_BOUND);
        for (Post post : posts.toList()) {
            post.setHostComment(post.getComments().get(0));
        }
        return posts;
    }

    @Override
    public Page<Post> findAll(Integer pageNum, Integer pageSize) throws PostException {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Post> posts = postRepository.findAll(pageable);
        if (posts.getTotalPages() < pageNum)
            throw new PostException(PostException.PostExceptionType.PAGE_OUT_OF_BOUND);
        for (Post post : posts.toList()) {
            post.setHostComment(post.getComments().get(0));
        }
        return posts;
    }
}
