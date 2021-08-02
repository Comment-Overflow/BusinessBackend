package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.request.NewCommentDTO;
import com.privateboat.forum.backend.dto.request.NewPostDTO;
import com.privateboat.forum.backend.dto.response.PageDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.enumerate.SortPolicy;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.interceptor.JWTInterceptor;
import com.privateboat.forum.backend.service.PostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerUnitTest {
    static final private PostTag TAG = PostTag.LIFE;
    static final private String TITLE = "TITLE";
    static final private String CONTENT = "CONTENT";
    static final private UserInfo USER_INFO = new UserInfo();
    static final private Post POST = new Post(TITLE, TAG);
    static final private Comment COMMENT = new Comment(POST, USER_INFO, 0L, CONTENT);
    static final private Integer PAGE_NUM = 1;
    static final private Integer PAGE_SIZE = 8;
    static final private Long USER_ID = 1L;
    static final private Long POST_ID = 5L;
    static final private Long COMMENT_ID = 1926L;
    static final private Long WRONG_USER_ID = 114514L;
    static final private Long WRONG_POST_ID = 1919810L;
    static final private Long WRONG_COMMENT_ID = 817L;
    static final private Long QUOTE_ID = 914L;
    static final private SortPolicy policy = SortPolicy.EARLIEST;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PostService postService;

    @MockBean
    private JWTInterceptor jwtInterceptor;

    @BeforeEach
    void setUp() {
        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        POST.addComment(COMMENT);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetPosts() throws Exception {
        Pageable pageable = PageRequest.of(PAGE_NUM, PAGE_SIZE);
        List<Post> postList = new ArrayList<>();
        for (long i = 1; i <= 20; ++i) {
            Post post = new Post(TITLE, TAG);
            post.setId(i);
            post.setHostComment(COMMENT);
            post.addComment(COMMENT);
            postList.add(post);
        }
        Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

        given(postService.findAll(PAGE_NUM, PAGE_SIZE, USER_ID)).willReturn(postPage);
        given(postService.findByTag(TAG, PAGE_NUM, PAGE_SIZE, USER_ID)).willReturn(postPage);

        Mockito.doThrow(new PostException(PostException.PostExceptionType.POSTER_NOT_EXIST))
                .when(postService)
                .findAll(PAGE_NUM, PAGE_SIZE, WRONG_USER_ID);

        Mockito.doThrow(new PostException(PostException.PostExceptionType.POSTER_NOT_EXIST))
                .when(postService)
                .findByTag(TAG, PAGE_NUM, PAGE_SIZE, WRONG_USER_ID);

        // Test get posts without tag.
        mvc.perform(get("/posts")
                .param("pageNum", PAGE_NUM.toString())
                .param("pageSize", PAGE_SIZE.toString())
                .param("followingOnly", "false")
                .requestAttr("userId", USER_ID))
                .andExpect(status().isOk());

        // Test get posts with tag.
        mvc.perform(get("/posts")
                        .param("tag", TAG.toString())
                        .param("pageNum", PAGE_NUM.toString())
                        .param("pageSize", PAGE_SIZE.toString())
                        .param("followingOnly", "false")
                        .requestAttr("userId", USER_ID))
                .andExpect(status().isOk());

        // Test user not exist
        mvc.perform(get("/posts")
                        .param("pageNum", PAGE_NUM.toString())
                        .param("pageSize", PAGE_SIZE.toString())
                        .param("followingOnly", "false")
                        .requestAttr("userId", WRONG_USER_ID))
                .andExpect(status().isConflict());

        mvc.perform(get("/posts")
                        .param("tag", TAG.toString())
                        .param("pageNum", PAGE_NUM.toString())
                        .param("pageSize", PAGE_SIZE.toString())
                        .param("followingOnly", "false")
                        .requestAttr("userId", WRONG_USER_ID))
                .andExpect(status().isConflict());
    }

    @Test
    void testPostPost() throws Exception {

        NewPostDTO newPostDTO = new NewPostDTO();
        newPostDTO.setTitle(TITLE);
        newPostDTO.setContent(CONTENT);
        newPostDTO.setTag(TAG);
        newPostDTO.setUploadFiles(new ArrayList<>());

        given(postService.postPost(USER_ID, newPostDTO)).willReturn(POST);
        Mockito.doThrow(new PostException(PostException.PostExceptionType.POSTER_NOT_EXIST))
                .when(postService)
                .postPost(WRONG_USER_ID, newPostDTO);

        // Test post a Post
        mvc.perform(post("/post")
                .param("title", TITLE)
                .param("tag", TAG.toString())
                .param("content", CONTENT)
                .requestAttr("userId", USER_ID))
                .andExpect(status().isOk());

        mvc.perform(post("/post")
                        .param("title", TITLE)
                        .param("tag", TAG.toString())
                        .param("content", CONTENT)
                        .requestAttr("userId", WRONG_USER_ID))
                .andExpect(status().isConflict());
    }

    @Test
    void testPostComment() throws Exception {
        NewCommentDTO commentDTO = new NewCommentDTO();
        commentDTO.setPostId(POST_ID);
        commentDTO.setQuoteId(QUOTE_ID);
        commentDTO.setContent(CONTENT);

        given(postService.postComment(USER_ID, commentDTO)).willReturn(COMMENT);
        Mockito.doThrow(new PostException(PostException.PostExceptionType.POSTER_NOT_EXIST))
                .when(postService)
                .postComment(WRONG_USER_ID, commentDTO);

        mvc.perform(post("/comment")
                        .param("postId", POST_ID.toString())
                        .param("quoteId", QUOTE_ID.toString())
                        .param("content", CONTENT)
                        .requestAttr("userId", USER_ID))
                .andExpect(status().isOk());

        mvc.perform(post("/comment")
                        .param("postId", POST_ID.toString())
                        .param("quoteId", QUOTE_ID.toString())
                        .param("content", CONTENT)
                        .requestAttr("userId", WRONG_USER_ID))
                .andExpect(status().isConflict());

    }

    @Test
    void testGetPost() throws Exception {
        given(postService.getPost(POST_ID, USER_ID)).willReturn(POST);
        Mockito.doThrow(new PostException(PostException.PostExceptionType.POST_NOT_EXIST))
                .when(postService)
                .getPost(WRONG_POST_ID, USER_ID);
        Mockito.doThrow(new PostException(PostException.PostExceptionType.VIEWER_NOT_EXIST))
                .when(postService)
                .getPost(POST_ID, WRONG_USER_ID);

        // Test get a post successfully
        mvc.perform(get("/post")
                .param("postId", POST_ID.toString())
                .requestAttr("userId", USER_ID))
                .andExpect(status().isOk());

        // Test get a wrong post
        mvc.perform(get("/post")
                        .param("postId", WRONG_POST_ID.toString())
                        .requestAttr("userId", USER_ID))
                .andExpect(status().isConflict());

        // Test get a post with wrong user
        mvc.perform(get("/post")
                        .param("postId", POST_ID.toString())
                        .requestAttr("userId", WRONG_USER_ID))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetPostComments() throws Exception {
        Pageable pageable = PageRequest.of(PAGE_NUM, PAGE_SIZE);
        List<Comment> comments = new ArrayList<>();
        for (long i = 1; i <= 20; ++i) {
            comments.add(COMMENT);
        }
        PageDTO<Comment> commentPageDTO = new PageDTO<>(new PageImpl<>(comments, pageable, comments.size()));

        given(postService.findByPostIdOrderByPolicy(POST_ID, policy, PAGE_NUM, PAGE_SIZE, USER_ID))
                .willReturn(commentPageDTO);
        Mockito.doThrow(new PostException(PostException.PostExceptionType.VIEWER_NOT_EXIST))
                .when(postService)
                .findByPostIdOrderByPolicy(POST_ID, policy, PAGE_NUM, PAGE_SIZE, WRONG_USER_ID);

        // get comments successfully
        mvc.perform(get("/post/comments")
                .param("postId", POST_ID.toString())
                .param("policy", policy.toString())
                .param("pageNum", PAGE_NUM.toString())
                .param("pageSize", PAGE_SIZE.toString())
                .requestAttr("userId", USER_ID))
                .andExpect(status().isOk());

        // get comments wrong
        mvc.perform(get("/post/comments")
                        .param("postId", POST_ID.toString())
                        .param("policy", policy.toString())
                        .param("pageNum", PAGE_NUM.toString())
                        .param("pageSize", PAGE_SIZE.toString())
                        .requestAttr("userId", WRONG_USER_ID))
                .andExpect(status().isConflict());

    }

    @Test
    void testDeletePost() throws Exception {
        Mockito.doThrow(new PostException(PostException.PostExceptionType.POST_NOT_EXIST))
                .when(postService)
                .deletePost(WRONG_POST_ID);

        // delete normal
        mvc.perform(delete("/post")
                .param("postId", POST_ID.toString()))
                .andExpect(status().isOk());

        mvc.perform(delete("/post")
                        .param("postId", WRONG_POST_ID.toString()))
                .andExpect(status().isConflict());

    }

    @Test
    void testDeleteComment() throws Exception {
        Mockito.doThrow(new PostException(PostException.PostExceptionType.COMMENT_NOT_EXIST))
                .when(postService)
                .deleteComment(WRONG_COMMENT_ID);

        // delete normal
        mvc.perform(delete("/comment")
                        .param("commentId", COMMENT_ID.toString()))
                .andExpect(status().isOk());

        mvc.perform(delete("/comment")
                        .param("commentId", WRONG_COMMENT_ID.toString()))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetPostByCommentId() throws Exception {
        given(postService.getPostByComment(COMMENT_ID, USER_ID)).willReturn(POST);
        Mockito.doThrow(new PostException(PostException.PostExceptionType.COMMENT_NOT_EXIST))
                .when(postService)
                .getPostByComment(WRONG_COMMENT_ID, USER_ID);
        Mockito.doThrow(new PostException(PostException.PostExceptionType.VIEWER_NOT_EXIST))
                .when(postService)
                .getPostByComment(COMMENT_ID, WRONG_USER_ID);

        mvc.perform(get("/comment/post")
                .param("commentId", COMMENT_ID.toString())
                .requestAttr("userId", USER_ID))
                .andExpect(status().isOk());

        mvc.perform(get("/comment/post")
                        .param("commentId", WRONG_COMMENT_ID.toString())
                        .requestAttr("userId", USER_ID))
                .andExpect(status().isConflict());

        mvc.perform(get("/comment/post")
                        .param("commentId", COMMENT_ID.toString())
                        .requestAttr("userId", WRONG_USER_ID))
                .andExpect(status().isConflict());

    }
}