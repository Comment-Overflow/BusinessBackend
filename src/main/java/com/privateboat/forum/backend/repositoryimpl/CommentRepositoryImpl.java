package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.CommentDAO;
import com.privateboat.forum.backend.dto.QuoteDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class CommentRepositoryImpl implements CommentRepository  {
    private final CommentDAO commentDAO;

    @Override
    public Page<Comment> findByPostId(Long postId, Pageable pageable) {
        return commentDAO.findByPostId(postId, pageable);
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
}
