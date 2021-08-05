package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.response.SearchedCommentDTO;
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
    public List<SearchedCommentDTO> searchComments(String searchKey, Pageable pageable) {
        List<Comment> searchedComments = commentRepository.findByContentContainingOrPostTitleContainingAndIsDeleted(
                searchKey, false, pageable).getContent();
        removeQuoteId(searchedComments);
        return wrapSearchedCommentsWithPost(searchedComments);
    }

    @Override
    public List<SearchedCommentDTO> searchCommentsByPostTag(PostTag postTag, String searchKey, Pageable pageable) {
        List<Comment> searchedComments = commentRepository.findByPostTag(postTag, searchKey, pageable).getContent();
        removeQuoteId(searchedComments);
        return wrapSearchedCommentsWithPost(searchedComments);
    }

    @Override
    public List<SearchedCommentDTO> searchCommentsByFollowingUsers(Long userId, Pageable pageable) {
        List<Comment> searchedComments = commentRepository.findByFollowingOnly(userId, pageable).getContent();
        removeQuoteId(searchedComments);
        return wrapSearchedCommentsWithPost(searchedComments);
    }

    private List<SearchedCommentDTO> wrapSearchedCommentsWithPost(List<Comment> comments) {
        return comments.stream().map(comment -> {
            Post parentPost = comment.getPost();
            postService.setPostTransientField(parentPost, comment.getUserInfo());
            return new SearchedCommentDTO(parentPost, comment);
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
                    followStatus,
                    userInfo.getUserAuth().getUserType()
            );
        }).collect(Collectors.toList());
    }

    private void removeQuoteId(List<Comment> comments) {
        for (Comment comment: comments) {
            comment.setQuoteId(0L);
        }
    }
}
