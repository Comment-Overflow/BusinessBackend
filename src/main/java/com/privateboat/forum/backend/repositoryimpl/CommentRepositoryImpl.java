package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.CommentDAO;
import com.privateboat.forum.backend.dto.QuoteDTO;
import com.privateboat.forum.backend.dto.response.PageDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CommentRepositoryImpl implements CommentRepository  {
    private final CommentDAO commentDAO;

    @Override
    @Cacheable(value = "post-cache", key = "#p0 + '-' + #p1.pageNumber + '-' + #p1.pageSize")
    public PageDTO<Comment> findByPostId(Long postId, Pageable pageable) {
        System.out.println("entering database...");
        Page<Comment> commentPage = commentDAO.findByPostId(postId, pageable);
        for (Comment comment : commentPage.getContent()) {
            comment.setUserInfo((UserInfo) Hibernate.unproxy(comment.getUserInfo()));
            comment.setImageUrl(new ArrayList<>(comment.getImageUrl()));
        }
        return new PageDTO<>(commentPage);
    }

    @Override
    @CachePut(value = "post-cache", key = "#p0 + '-' + #p1.pageNumber + '-' + #p1.pageSize")
    public PageDTO<Comment> updateCommentCache(Long postId, Pageable pageable) {
        // Calling function annotated by @Cacheable in the same class won't be cached.
        return findByPostId(postId, pageable);
    }

    @Override
    public Comment save(Comment comment) {
        return commentDAO.save(comment);
    }

    @Override
    public Optional<Comment> findById(Long id){
        return commentDAO.findById(id);
    }

    @Override
    public Comment getById(Long id) throws PostException {
        try {
            return commentDAO.getById(id);
        } catch (PostException e){
            throw new PostException(PostException.PostExceptionType.COMMENT_NOT_EXIST);
        }
    }

    @Override
    public QuoteDTO getCommentAsQuote(Long commentId) throws PostException {
        return new QuoteDTO(getById(commentId));
    }

    @Override
    public Page<Comment> findByContentContainingOrPostTitleContainingAndIsDeleted(
            String searchKey,
            Boolean isDeleted,
            Pageable pageable) {
        return commentDAO.findByContentContainingOrPostTitleContainingAndPostIsDeleted(
                searchKey,
                false,
                pageable);
    }

    @Override
    public Page<Comment> findByPostTag(PostTag postTag, String searchKey, Pageable pageable) {
        return commentDAO.findByPostTagAndContentContainingOrPostTitleContainingAndPostIsDeleted(
                postTag,
                searchKey,
                false,
                pageable);
    }

    @Override
    public Page<Comment> findByFollowingOnly(Long userId, Pageable pageable) {
        return commentDAO.findByFollowingOnly(userId, pageable);
    }

    @Override
    public Page<Comment> getMyComments(Long userId, Pageable pageable) {
        return commentDAO.findByUserInfoIdAndFloorIsNotAndIsDeletedOrderByTimeDesc(userId, 0,false, pageable);
    }

    @Override
    public void delete(Comment comment) {
        comment.setIsDeleted(true);
        commentDAO.save(comment);
    }
}
