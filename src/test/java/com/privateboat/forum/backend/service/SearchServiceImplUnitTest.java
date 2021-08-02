package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.response.SearchedCommentDTO;
import com.privateboat.forum.backend.dto.response.UserCardInfoDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.SearchHistory;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.repository.CommentRepository;
import com.privateboat.forum.backend.repository.FollowRecordRepository;
import com.privateboat.forum.backend.repository.SearchHistoryRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.serviceimpl.SearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.privateboat.forum.backend.fakedata.UserData.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class SearchServiceImplUnitTest {

    @InjectMocks
    private SearchServiceImpl searchService;

    @Mock
    private PostService postService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @Mock
    private FollowRecordRepository followRecordRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 8);

    private static final String TITLE = "abc";
    private static final String CONTENT = "def";

    private static final PostTag POST_TAG1 = PostTag.LIFE;
    private static final PostTag POST_TAG2 = PostTag.CAREER;

    private static Comment COMMENT1;
    private static Comment COMMENT2;
    private static List<Comment> COMMENTS;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Post POST1 = new Post(TITLE, POST_TAG1);
        Post POST2 = new Post(TITLE, POST_TAG2);

        COMMENT1 = new Comment(POST1, USER_INFO, 0L, CONTENT);
        COMMENT2 = new Comment(POST2, USER_INFO, 0L, CONTENT);
        COMMENTS = new ArrayList<>() {{
            add(COMMENT1);
            add(COMMENT2);
        }};
        POST1.setComments(COMMENTS);
        POST2.setComments(COMMENTS);
    }

    @Test
    void testSearchComments() {
        given(commentRepository.findByContentContainingOrPostTitleContainingAndIsDeleted(
                "abc",
                false,
                PAGE_REQUEST)
        )
                .willReturn(new PageImpl<>(
                        COMMENTS.stream().filter(
                        comment -> comment.getContent().contains("abc") ||
                                comment.getPost().getTitle().contains("abc")
                        ).collect(Collectors.toList())
                ));
        List<SearchedCommentDTO> searchedComments = searchService.searchComments("abc", PAGE_REQUEST);
        for (SearchedCommentDTO parentPost: searchedComments) {
            Comment comment = parentPost.getSearchedComment();
            assertTrue(comment.getContent().contains("abc") || comment.getPost().getTitle().contains("abc"));
        }
    }

    @Test
    void testSearchCommentsByPostTag() {
        given(commentRepository.findByPostTag(PostTag.LIFE, "abc", PAGE_REQUEST))
                .willReturn(new PageImpl<>(
                        COMMENTS.stream().filter(
                                comment ->
                                       (comment.getContent().contains("abc") ||
                                        comment.getPost().getTitle().contains("abc")) &&
                                                comment.getPost().getTag().equals(PostTag.LIFE)
                        ).collect(Collectors.toList())
                ));
        Page<Comment> searchedComments = commentRepository.findByPostTag(PostTag.LIFE, "abc", PAGE_REQUEST);
        for (Comment comment: searchedComments) {
            assertTrue((comment.getContent().contains("abc") ||
                    comment.getPost().getTitle().contains("abc")) &&
                    comment.getPost().getTag().equals(PostTag.LIFE));
        }
    }

    @Test
    void addSearchHistory() {
        searchService.addSearchHistory(USER_ID, "abc", PostTag.LIFE);
        Mockito.verify(searchHistoryRepository).save(any(SearchHistory.class));
    }

    @Test
    void testSearchUsers() {
        List<UserInfo> userInfoList = new ArrayList<>() {{
            add(USER_INFO);
        }};

        given(userInfoRepository.findByUserNameContaining(USER_NAME)).
                willReturn(userInfoList);

        List<UserCardInfoDTO> searchedUserInfo = searchService.searchUsers(USER_ID, USER_NAME);
        for (UserCardInfoDTO userInfo : searchedUserInfo) {
            assertTrue(userInfo.getUserName().contains(USER_NAME));
        }
    }
}
