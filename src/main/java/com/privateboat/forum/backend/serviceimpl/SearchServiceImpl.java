package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.response.UserCardInfoDTO;
import com.privateboat.forum.backend.entity.*;
import com.privateboat.forum.backend.enumerate.FollowStatus;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.repository.*;
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
    private final StarRecordRepository starRecordRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final UserInfoRepository userInfoRepository;
    private final FollowRecordRepository followRecordRepository;

    @Override
    public Page<Comment> searchComments(String searchKey, Pageable pageable) {
        return commentRepository.searchAll(searchKey, pageable);
    }

    @Override
    public Page<Comment> searchCommentsByPostTag(PostTag postTag, String searchKey, Pageable pageable) {
        return commentRepository.searchByTag(postTag, searchKey, pageable);
    }

    @Override
    public List<Post> wrappedSearchedCommentsWithPost(Long userId, List<Comment> comments) {
        return comments.stream().map(comment -> {
            Post parentPost = comment.getPost();
            parentPost.setHostComment(comment);
            parentPost.setIsStarred(starRecordRepository.checkIfHaveStarred(comment.getUserInfo(), parentPost));
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
                    userStatistic.getApprovalCount(),
                    followStatus
            );
        }).collect(Collectors.toList());
    }
}
