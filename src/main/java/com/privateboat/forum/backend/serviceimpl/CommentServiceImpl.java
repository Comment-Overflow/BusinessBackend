package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.repository.CommentRepository;
import com.privateboat.forum.backend.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    @Override
    public Page<Comment> searchAll(String searchKey, Pageable pageable) {
        return commentRepository.searchAll(searchKey, pageable);
    }

    @Override
    public Page<Comment> searchByPostTag(PostTag postTag, String searchKey, Pageable pageable) {
        return commentRepository.searchByTag(postTag, searchKey, pageable);
    }
}
