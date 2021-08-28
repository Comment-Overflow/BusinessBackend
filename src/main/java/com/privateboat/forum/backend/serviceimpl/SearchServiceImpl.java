package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.response.SearchedCommentDTO;
import com.privateboat.forum.backend.dto.response.UserCardInfoDTO;
import com.privateboat.forum.backend.entity.*;
import com.privateboat.forum.backend.enumerate.FollowStatus;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.repository.CommentRepository;
import com.privateboat.forum.backend.repository.FollowRecordRepository;
import com.privateboat.forum.backend.repository.PostRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.PostService;
import com.privateboat.forum.backend.service.SearchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserInfoRepository userInfoRepository;
    private final FollowRecordRepository followRecordRepository;
    private final PostService postService;

    @Override
    public List<SearchedCommentDTO> searchComments(String searchKey, Pageable pageable) {
        // Get half of the results from post, half of them from comment.
        AtomicReference<List<Comment>> searchedCommentsReference = new AtomicReference<>();
        AtomicReference<List<Post>> searchedPostsReference = new AtomicReference<>();

        Thread commentThread = new Thread(
                () -> {
                    long a = System.currentTimeMillis();
                    searchedCommentsReference.set(commentRepository.findByContentContainingAndIsDeleted(
                            searchKey, false, pageable).getContent());
                    long b = System.currentTimeMillis();
                    log.info(String.format("<findByPostTag> elapsed time: %d%n", b - a));
                }
        );

        Thread postThread = new Thread(
                () -> {
                    searchedPostsReference.set(postRepository.findByTitleContainingAndIsDeletedOrderByPostTime(
                            searchKey, false, pageable
                    ).getContent());
                }
        );

        try {
            commentThread.join();
            postThread.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

        List<SearchedCommentDTO> searchedPosts = searchedPostsReference.get().stream().map(
                (post) -> new SearchedCommentDTO(post, post.getHostComment())
        ).collect(Collectors.toList());
        List<SearchedCommentDTO> searchedComments = searchedCommentsReference.get().stream().map(
                (comment) -> new SearchedCommentDTO(comment.getPost(), comment)
        ).collect(Collectors.toList());

        // TODO: merge the results
        removeQuoteId(searchedCommentsReference.get());

        long a = System.currentTimeMillis();
        List<SearchedCommentDTO> ret = wrapSearchedCommentsWithPost(searchedCommentsReference.get());
        long b = System.currentTimeMillis();
        log.info(String.format("<wrapSearchedCommentsWithPost> elapsed time: %d%n", b - a));

        return ret;
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
