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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
        List<Comment> contentList = commentPage.getContent();
        if (contentList.size() == 0) {
            return new PageDTO<>(commentPage);
        }
        for (Comment comment : contentList) {
            comment.setUserInfo((UserInfo) Hibernate.unproxy(comment.getUserInfo()));
            comment.setImageUrl(new ArrayList<>(comment.getImageUrl()));
        }

        for (int index = 0; index < contentList.size(); ++index) {
            contentList.set(index, (Comment) Hibernate.unproxy(contentList.get(index)));
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Comment saveAndFlush(Comment newComment) {
        return commentDAO.saveAndFlush(newComment);
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
    public Page<Comment> findByContentContainingAndIsDeleted(
            String searchKey,
            Boolean isDeleted,
            Pageable pageable) {
        return commentDAO.findByContentContainingAndPostIsDeleted(
                searchKey,
                false,
                pageable);
    }

    @Override
    public Page<Comment> findByPostTag(PostTag postTag, String searchKey, Pageable pageable) {
        return commentDAO.findByPostTagAndContentContainingAndPostIsDeleted(
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
    public Page<Comment> getOnesComments(Long userId, Pageable pageable) {
        return commentDAO.findByUserInfoIdAndFloorGreaterThanAndIsDeletedOrderByTimeDesc(userId, 0, false, pageable);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void setIsDeletedAndFlush(Comment comment) {
        comment.setIsDeleted(true);
        commentDAO.save(comment);
    }

    @Override
    public boolean existsById(Long commentId) {
        return commentDAO.existsById(commentId);
    }

    @Override
    public void deleteCommentsByPostId(Long postId) {
        commentDAO.deleteCommentsByPostId(postId);
    }
}
