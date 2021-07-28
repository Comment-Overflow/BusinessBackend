package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.CommentDAO;
import com.privateboat.forum.backend.dto.QuoteDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class CommentRepositoryImpl implements CommentRepository  {
    private final CommentDAO commentDAO;

    @Override
    public Page<Comment> findByPostId(Long postId, Pageable pageable) {
        return commentDAO.findByPostIdAndIsDeleted(postId, false, pageable);
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
    public Page<Comment> searchAll(String searchKey, Pageable pageable) {
        return commentDAO.findByContentContainingOrPostTitleContaining(searchKey, pageable);
    }

    @Override
    public Page<Comment> searchByTag(PostTag postTag, String searchKey, Pageable pageable) {
        return commentDAO.findByPostTagAndContentContainingOrPostTitleContaining(postTag, searchKey, pageable);
    }

    public void delete(Comment comment) {
        comment.setIsDeleted(true);
        commentDAO.save(comment);
    }
}
