package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.CommentDAO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

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
}
