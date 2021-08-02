package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.request.NewCommentDTO;
import com.privateboat.forum.backend.dto.request.NewPostDTO;
import com.privateboat.forum.backend.dto.response.PageDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.entity.UserStatistic;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.enumerate.SortPolicy;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.repository.*;
import com.privateboat.forum.backend.serviceimpl.PostServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class PostServiceImplUnitTest {
    static private UserInfo USER_INFO;
    static final private Long USER_ID = 1L;
    static final private Long COMMENT_USER_ID = 3L;
    static final private Long WRONG_USER_ID = 114L;
    static final private Long POST_ID = 5L;
    static final private Long WRONG_POST_ID = 514L;
    static final private Long COMMENT_ID = 9L;
    static final private Long WRONG_COMMENT_ID = 1919L;
    static final private PostTag TAG = PostTag.LIFE;
    static final private SortPolicy POLICY = SortPolicy.EARLIEST;
    static final private Integer PAGE_NUM = 1;
    static final private Integer PAGE_SIZE = 8;
    static final private Pageable PAGEABLE = PageRequest.of(PAGE_NUM, PAGE_SIZE);
    static final private Pageable SORTED_PAGEABLE = PageRequest.of(PAGE_NUM, PAGE_SIZE,
            Sort.by(Sort.Direction.ASC, "floor"));
    static final private String TITLE = "TITLE";
    static final private String CONTENT = "CONTENT";
    static final private Long TOTAL_SIZE = 20L;
    static private NewPostDTO NEW_POST_DTO;
    static final private Long QUOTE_ID = 0L;
    static private NewCommentDTO NEW_COMMENT_DTO;
    static private NewCommentDTO WRONG_NEW_COMMENT_DTO;


    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private ApprovalRecordRepository approvalRecordRepository;

    @Mock
    private StarRecordRepository starRecordRepository;

    @Mock
    private UserStatisticRepository userStatisticRepository;

    @Mock
    private ReplyRecordService replyRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        USER_INFO = new UserInfo();
        USER_INFO.setId(USER_ID);
        UserStatistic USER_STATISTIC = new UserStatistic(USER_INFO);
        USER_INFO.setUserStatistic(USER_STATISTIC);
        Post POST = new Post(TITLE, TAG);
        Comment COMMENT = new Comment(POST, USER_INFO, 0L, CONTENT);
        COMMENT.setId(COMMENT_ID);
        POST.setHostComment(COMMENT);
        POST.addComment(COMMENT);

        List<Post> postList = new ArrayList<>();
        for (long i = 1; i <= TOTAL_SIZE; ++i) {
            postList.add(POST);
        }
        Page<Post> POST_PAGE = new PageImpl<>(postList, PAGEABLE, postList.size());

        List<Comment> commentList = new ArrayList<>();
        for (long i = 1; i <= TOTAL_SIZE; ++i) {
            commentList.add(COMMENT);
        }
        Page<Comment> COMMENT_PAGE =
                new PageImpl<>(commentList, SORTED_PAGEABLE, commentList.size());

        NEW_POST_DTO = new NewPostDTO();
        NEW_POST_DTO.setTitle(TITLE);
        NEW_POST_DTO.setTag(TAG);
        NEW_POST_DTO.setContent(CONTENT);

        NEW_COMMENT_DTO = new NewCommentDTO();
        NEW_COMMENT_DTO.setPostId(POST_ID);
        NEW_COMMENT_DTO.setQuoteId(QUOTE_ID);
        NEW_COMMENT_DTO.setContent(CONTENT);

        WRONG_NEW_COMMENT_DTO = new NewCommentDTO();
        WRONG_NEW_COMMENT_DTO.setPostId(WRONG_POST_ID);
        WRONG_NEW_COMMENT_DTO.setQuoteId(QUOTE_ID);
        WRONG_NEW_COMMENT_DTO.setContent(CONTENT);

        Mockito.when(userInfoRepository.findByUserId(USER_ID))
                .thenReturn(Optional.of(USER_INFO));
        Mockito.when(userInfoRepository.findByUserId(COMMENT_USER_ID))
                .thenReturn(Optional.of(USER_INFO));
        Mockito.when(userInfoRepository.findByUserId(WRONG_USER_ID))
                .thenReturn(Optional.empty());
        Mockito.when(postRepository.findByTag(TAG, PAGEABLE))
                .thenReturn(POST_PAGE);
        Mockito.when(postRepository.findAll(PAGEABLE))
                .thenReturn(POST_PAGE);
        Mockito.when(postRepository.findByPostId(POST_ID))
                .thenReturn(Optional.of(POST));
        Mockito.when(postRepository.findByPostId(WRONG_POST_ID))
                .thenReturn(Optional.empty());
        Mockito.when(approvalRecordRepository.checkIfHaveApproved(USER_INFO, COMMENT))
                .thenReturn(ApprovalStatus.APPROVAL);
        Mockito.when(starRecordRepository.checkIfHaveStarred(USER_INFO, POST))
                .thenReturn(true);
        Mockito.when(commentRepository.findById(COMMENT_ID))
                .thenReturn(Optional.of(COMMENT));
        Mockito.when(commentRepository.findById(WRONG_COMMENT_ID))
                .thenReturn(Optional.empty());
        Mockito.when(commentRepository.findByPostId(POST_ID, SORTED_PAGEABLE))
                .thenReturn(COMMENT_PAGE);

    }

    @Test
    void testFindByTag() {
        try {
            Page<Post> result = postService.findByTag(TAG, PAGE_NUM, PAGE_SIZE, USER_ID);
            assertSame(result.getSize(), PAGE_SIZE);
            assertSame(result.getTotalElements(), TOTAL_SIZE);
            for (Post post: result.getContent()) {
                assertNotNull(post.getIsStarred());
                assertNotNull(post.getHostComment().getApprovalStatus());
            }
        } catch (PostException e) {
            assertNull(e);
        }

        PostException wrongUserException =
                Assertions.assertThrows(PostException.class, () -> postService
                        .findByTag(TAG, PAGE_NUM, PAGE_SIZE, WRONG_USER_ID));
        assertSame(wrongUserException.getType(),
                PostException.PostExceptionType.VIEWER_NOT_EXIST);
    }

    @Test
    void testFindAll() {
        try {
            Page<Post> result = postService.findAll(PAGE_NUM, PAGE_SIZE, USER_ID);
            assertSame(result.getSize(), PAGE_SIZE);
            assertSame(result.getTotalElements(), TOTAL_SIZE);
            for (Post post: result.getContent()) {
                assertNotNull(post.getIsStarred());
                assertNotNull(post.getHostComment().getApprovalStatus());
            }
        } catch (PostException e) {
            assertNull(e);
        }

        PostException wrongUserException =
                Assertions.assertThrows(PostException.class, () -> postService
                        .findAll(PAGE_NUM, PAGE_SIZE, WRONG_USER_ID));
        assertSame(wrongUserException.getType(),
                PostException.PostExceptionType.VIEWER_NOT_EXIST);
    }

    @Test
    void testPostPost() {
        Post result = postService.postPost(USER_ID, NEW_POST_DTO);
        assertSame(result.getUserInfo(), USER_INFO);
        assertSame(result.getTitle(), TITLE);
        assertSame(result.getTag(), TAG);
        assertSame(result.getIsDeleted(), false);
        assertSame(result.getCommentCount(), 1);
        Mockito.verify(userStatisticRepository).save(any());
        Mockito.verify(postRepository).save(any());
        Mockito.verify(commentRepository).save(any());

        PostException wrongUserException =
                Assertions.assertThrows(PostException.class, () -> postService
                        .postPost(WRONG_USER_ID, NEW_POST_DTO));
        assertSame(wrongUserException.getType(),
                PostException.PostExceptionType.POSTER_NOT_EXIST);
    }

    @Test
    void testPostComment() {
        Comment result = postService.postComment(COMMENT_USER_ID, NEW_COMMENT_DTO);
        assertSame(result.getContent(), CONTENT);
        assertSame(result.getIsDeleted(), false);
        assertSame(result.getQuoteId(), QUOTE_ID);
        Mockito.verify(userStatisticRepository).save(any());
        Mockito.verify(commentRepository).save(any());
        Mockito.verify(postRepository).save(any());

        PostException wrongUserException =
                Assertions.assertThrows(PostException.class, () -> postService
                        .postComment(WRONG_USER_ID, NEW_COMMENT_DTO));
        assertSame(wrongUserException.getType(),
                PostException.PostExceptionType.POSTER_NOT_EXIST);

        PostException wrongPostException =
                Assertions.assertThrows(PostException.class, () -> postService
                        .postComment(USER_ID, WRONG_NEW_COMMENT_DTO));
        assertSame(wrongPostException.getType(),
                PostException.PostExceptionType.POST_NOT_EXIST);
    }

    @Test
    void testGetPost() {
        Post result = postService.getPost(POST_ID, USER_ID);
        assertNotNull(result.getIsStarred());

        PostException wrongUserException =
                Assertions.assertThrows(PostException.class, () -> postService
                        .getPost(POST_ID, WRONG_USER_ID));
        assertSame(wrongUserException.getType(),
                PostException.PostExceptionType.VIEWER_NOT_EXIST);

        PostException wrongPostException =
                Assertions.assertThrows(PostException.class, () -> postService
                        .getPost(WRONG_POST_ID, USER_ID));
        assertSame(wrongPostException.getType(),
                PostException.PostExceptionType.POST_NOT_EXIST);
    }

    @Test
    void testGetPostByComment() {
        Post result = postService.getPostByComment(COMMENT_ID, USER_ID);
        assertNotNull(result.getIsStarred());

        PostException wrongUserException =
                Assertions.assertThrows(PostException.class, () -> postService
                        .getPostByComment(COMMENT_ID, WRONG_USER_ID));
        assertSame(wrongUserException.getType(),
                PostException.PostExceptionType.VIEWER_NOT_EXIST);

        PostException wrongPostException =
                Assertions.assertThrows(PostException.class, () -> postService
                        .getPostByComment(WRONG_COMMENT_ID, USER_ID));
        assertSame(wrongPostException.getType(),
                PostException.PostExceptionType.COMMENT_NOT_EXIST);
    }

    @Test
    void testFindByPostIdOrderByPolicy() {
        PageDTO<Comment> result = postService
                .findByPostIdOrderByPolicy(POST_ID, POLICY, PAGE_NUM, PAGE_SIZE, USER_ID);
        assertSame(result.getSize(), TOTAL_SIZE);
        for (Comment comment: result.getContent()) {
            assertNotNull(comment.getApprovalStatus());
        }

        PostException wrongUserException =
                Assertions.assertThrows(PostException.class, () -> postService
                        .findByPostIdOrderByPolicy(POST_ID, POLICY, PAGE_NUM, PAGE_SIZE, WRONG_USER_ID));
        assertSame(wrongUserException.getType(),
                PostException.PostExceptionType.VIEWER_NOT_EXIST);
    }

    @Test
    void testDeletePost() {
        postService.deletePost(POST_ID, USER_ID);
        Mockito.verify(userStatisticRepository).save(any());
        Mockito.verify(postRepository).delete(any());

        PostException wrongPostException =
                Assertions.assertThrows(PostException.class, () -> postService
                        .deletePost(WRONG_POST_ID, USER_ID));
        assertSame(wrongPostException.getType(),
                PostException.PostExceptionType.POST_NOT_EXIST);
    }

    @Test
    void testDeleteComment() {
        postService.deleteComment(COMMENT_ID, USER_ID);
        Mockito.verify(userStatisticRepository).save(any());
        Mockito.verify(postRepository).save(any());
        Mockito.verify(commentRepository).delete(any());

        PostException wrongPostException =
                Assertions.assertThrows(PostException.class, () -> postService
                        .deleteComment(WRONG_COMMENT_ID, USER_ID));
        assertSame(wrongPostException.getType(),
                PostException.PostExceptionType.COMMENT_NOT_EXIST);
    }
}
