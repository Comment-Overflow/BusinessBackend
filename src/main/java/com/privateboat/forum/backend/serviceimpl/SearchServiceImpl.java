package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.repository.CommentRepository;
import com.privateboat.forum.backend.service.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final CommentRepository commentRepository;

    @Override
    public Page<Comment> searchComments(String searchKey, Pageable pageable) {
        return commentRepository.searchAll(searchKey, pageable);
    }

    @Override
    public Page<Comment> searchCommentsByPostTag(PostTag postTag, String searchKey, Pageable pageable) {
        return commentRepository.searchByTag(postTag, searchKey, pageable);
    }

    @Override
    public void addSearchHistory(Long userId, String searchKey) {

    }
}
