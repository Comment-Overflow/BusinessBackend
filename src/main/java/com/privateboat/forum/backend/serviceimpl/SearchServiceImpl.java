package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.response.UserCardInfoDTO;
import com.privateboat.forum.backend.entity.*;
import com.privateboat.forum.backend.enumerate.FollowStatus;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.repository.CommentRepository;
import com.privateboat.forum.backend.repository.FollowRecordRepository;
import com.privateboat.forum.backend.repository.SearchHistoryRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.PostService;
import com.privateboat.forum.backend.service.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final CommentRepository commentRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final UserInfoRepository userInfoRepository;
    private final FollowRecordRepository followRecordRepository;
    private final PostService postService;

    @Override
    public Page<Comment> searchComments(String searchKey, Pageable pageable) {
        return removeQuoteId(commentRepository.searchAll(searchKey, pageable));
    }

    @Override
    public Page<Comment> searchCommentsByPostTag(PostTag postTag, String searchKey, Pageable pageable) {
        return removeQuoteId(commentRepository.searchByTag(postTag, searchKey, pageable));
    }

    @Override
    public List<Post> wrapSearchedCommentsWithPost(List<Comment> comments) {
        return comments.stream().map(comment -> {
            Post parentPost = comment.getPost();
            postService.setPostTransientField(parentPost, comment.getUserInfo());
            parentPost.setSearchedComment(comment);
            return parentPost;
        }).collect(Collectors.toList());
    }

    @Override
    public void addSearchHistory(Long userId, String searchKey, PostTag postTag) {
        SearchHistory searchHistory = new SearchHistory(userId, searchKey, postTag);
        searchHistoryRepository.save(searchHistory);
    }

    @Override
    public List<UserCardInfoDTO> searchUsers(Long userId, String searchKey) {
        List<UserInfo> searchedUserInfo = userInfoRepository.findByUserNameContaining(searchKey);

        return searchedUserInfo.stream().map(userInfo -> {
            FollowStatus followStatus = followRecordRepository.getFollowStatus(userId, userInfo.getId());
            UserStatistic userStatistic = userInfo.getUserStatistic();
            return new UserCardInfoDTO(
                    userInfo.getId(),
                    userInfo.getUserName(),
                    userInfo.getAvatarUrl(),
                    userInfo.getBrief(),
                    userStatistic.getCommentCount(),
                    userStatistic.getFollowerCount(),
                    followStatus
            );
        }).collect(Collectors.toList());
    }

    private Page<Comment> removeQuoteId(Page<Comment> comments) {
        for (Comment comment: comments.getContent()) {
            comment.setQuoteId(0L);
        }
        return comments;
    }
}
