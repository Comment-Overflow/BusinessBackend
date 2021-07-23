package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.request.NewCommentDTO;
import com.privateboat.forum.backend.dto.request.NewPostDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.repository.CommentRepository;
import com.privateboat.forum.backend.repository.PostRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public Page<Post> findByTag(PostTag tag, Integer pageNum, Integer pageSize) throws PostException {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Post> posts = postRepository.findByTag(tag, pageable);
        if (posts.getTotalPages() < pageNum) {
            throw new PostException(PostException.PostExceptionType.PAGE_OUT_OF_BOUND);
        }
        return posts;
    }

    @Override
    public Page<Post> findAll(Integer pageNum, Integer pageSize) throws PostException {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Post> posts = postRepository.findAll(pageable);
        if (posts.getTotalPages() < pageNum) {
            throw new PostException(PostException.PostExceptionType.PAGE_OUT_OF_BOUND);
        }
        return posts;
    }

    @Override
    public Post postPost(Long userId, NewPostDTO newPostDTO) throws PostException {
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POSTER_NOT_EXIST);
        }
        Post post = new Post(newPostDTO.getTitle(), newPostDTO.getTag());
        Comment hostComment = new Comment(post, userInfo.get(), 0L, newPostDTO.getContent());
        post.setHostComment(hostComment);
        post.addComment(hostComment);
        postRepository.save(post);
        commentRepository.save(hostComment);
        return post;
    }

    @Override
    public Comment postComment(Long userId, NewCommentDTO commentDTO) throws PostException {
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POSTER_NOT_EXIST);
        }
        Optional<Post> post = postRepository.findByPostId(commentDTO.getPostId());
        if (post.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }
        Comment comment = new Comment(post.get(), userInfo.get(),
                commentDTO.getQuoteId(), commentDTO.getContent());
        post.get().addComment(comment);
        commentRepository.save(comment);
        return comment;
    }

    @Override
    public Post getPost(Long postId) throws PostException {
        Optional<Post> post = postRepository.findByPostId(postId);
        if (post.isEmpty()) {
            throw new PostException(PostException.PostExceptionType.POST_NOT_EXIST);
        }
        return post.get();
    }
}
