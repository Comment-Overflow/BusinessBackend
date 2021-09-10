package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.response.SearchedCommentDTO;
import com.privateboat.forum.backend.dto.response.UserCardInfoDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.entity.UserStatistic;
import com.privateboat.forum.backend.enumerate.FollowStatus;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.*;
import com.privateboat.forum.backend.service.PostService;
import com.privateboat.forum.backend.service.SearchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
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
    private final StarRecordRepository starRecordRepository;
    private final ApprovalRecordRepository approvalRecordRepository;
    private final PostService postService;

    private interface SearchInterface<T> {
        List<T> search();
    }

    @Override
    public List<SearchedCommentDTO> searchAllComments(Long userId, String searchKey, Pageable pageable)
            throws UserInfoException {
        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByUserId(userId);
        if (optionalUserInfo.isEmpty()) {
            throw new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);
        }
        UserInfo userInfo = optionalUserInfo.get();

        // Get half of the results from post, half of them from comment.
        Pair<List<Comment>, List<Post>> searchResults = searchComments(
                () -> commentRepository.findByContentContainingAndIsDeleted(
                        searchKey, false, pageable).getContent(),
                () -> postRepository.findByTitleContainingAndIsDeletedOrderByPostTime(
                        searchKey, false, pageable).getContent()
                );

        return processSearchResults(userInfo, searchResults.getFirst(), searchResults.getSecond());
    }

    @Override
    public List<SearchedCommentDTO> searchCommentsByPostTag(Long userId, PostTag postTag, String searchKey, Pageable pageable) {
        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByUserId(userId);
        if (optionalUserInfo.isEmpty()) {
            throw new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);
        }
        UserInfo userInfo = optionalUserInfo.get();

        Pair<List<Comment>, List<Post>> searchResults = searchComments(
                () -> commentRepository.findByPostTag(
                        postTag, searchKey, pageable).getContent(),
                () -> postRepository.findByTitleContainingAndTagAndIsDeletedOrderByPostTime(
                        searchKey, postTag, false, pageable).getContent()
        );

        return processSearchResults(userInfo, searchResults.getFirst(), searchResults.getSecond());
    }

    @Override
    public List<SearchedCommentDTO> searchCommentsByFollowingUsers(Long userId, Pageable pageable) {
        long a;
        a = System.currentTimeMillis();
        List<Comment> searchedComments = commentRepository.findByFollowingOnly(userId, pageable).getContent();
        log.info(String.format("========== findByFollowingOnly: %d", System.currentTimeMillis() - a));
        removeQuoteId(searchedComments);

        a = System.currentTimeMillis();
        List<SearchedCommentDTO> ret = wrapSearchedCommentsWithPost(searchedComments);
        log.info(String.format("========== wrap: %d", System.currentTimeMillis() - a));
        return ret;
    }

    private List<SearchedCommentDTO> wrapSearchedCommentsWithPost(List<Comment> comments) {
        return comments.stream().map(comment -> {
            Post parentPost = comment.getPost();
            postService.setPostApprovalStatusAndIsStarred(parentPost, comment.getUserInfo());
            SearchedCommentDTO dto = new SearchedCommentDTO(parentPost, comment);
            // isStarred is no longer set upon constructor invocation.
            dto.setIsStarred(parentPost.getIsStarred());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<UserCardInfoDTO> searchUsers(Long userId, String searchKey) {
        List<UserInfo> searchedUserInfo = userInfoRepository.findByUserNameContainingIgnoreCase(searchKey);

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

    private List<SearchedCommentDTO> processSearchResults(
            UserInfo userInfo,
            List<Comment> searchedComments,
            List<Post> searchedPosts
    ) {
        List<SearchedCommentDTO> comments = searchedComments.stream().map(
                (comment) -> new SearchedCommentDTO(comment.getPost(), comment)
        ).collect(Collectors.toList());
        List<SearchedCommentDTO> posts = searchedPosts.stream().map(
                (post) -> new SearchedCommentDTO(post, post.getHostComment())
        ).collect(Collectors.toList());

        // Merge and remove duplicate.
        Set<SearchedCommentDTO> s = new LinkedHashSet<>(comments);
        s.addAll(posts);
        List<SearchedCommentDTO> results = new ArrayList<>(s);

        // Sort by comment time.
        results.sort((r1, r2) -> -r1.getSearchedComment().getTime().compareTo(r2.getSearchedComment().getTime()));

        // Set isStarred and isApproved.
        for (SearchedCommentDTO result: results) {
            Comment hostComment = result.getHostComment();
            result.getSearchedComment().setQuoteId(0L);
            result.setIsStarred(starRecordRepository.checkIfHaveStarred(userInfo, result.getPost()));
            hostComment.setApprovalStatus(approvalRecordRepository.checkIfHaveApproved(userInfo, hostComment));
        }

        return results;
    }

    private Pair<List<Comment>, List<Post>> searchComments(
            SearchInterface<Comment> searchCommentsInterface,
            SearchInterface<Post> searchPostsInterface) {
        AtomicReference<List<Comment>> searchedCommentsReference = new AtomicReference<>();
        AtomicReference<List<Post>> searchedPostsReference = new AtomicReference<>();

        searchedCommentsReference.set(searchCommentsInterface.search());

        searchedPostsReference.set(searchPostsInterface.search());

        return Pair.of(
                searchedCommentsReference.get(),
                searchedPostsReference.get()
        );
    }
}
