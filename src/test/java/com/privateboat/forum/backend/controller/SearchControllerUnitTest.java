package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.response.SearchedCommentDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.entity.UserStatistic;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.interceptor.JWTInterceptor;
import com.privateboat.forum.backend.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.privateboat.forum.backend.fakedata.UserData.USER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
public class SearchControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SearchService searchService;

    @MockBean
    private JWTInterceptor jwtInterceptor;

    static final private Long COMMENT_ID = 9L;
    static final private String TITLE = "TITLE";
    static final private String CONTENT = "CONTENT";
    static final private PostTag TAG = PostTag.LIFE;

    @BeforeEach
    void setUp() {
        UserInfo USER_INFO = new UserInfo();
        USER_INFO.setId(USER_ID);
        UserStatistic USER_STATISTIC = new UserStatistic(USER_INFO);
        USER_INFO.setUserStatistic(USER_STATISTIC);
        Post POST = new Post(TITLE, TAG);
        Comment COMMENT = new Comment(POST, USER_INFO, 0L, CONTENT);
        COMMENT.setId(COMMENT_ID);
        POST.setHostComment(COMMENT);
        POST.addComment(COMMENT);

        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void testSearchComments() throws Exception {
        List<SearchedCommentDTO> commentPage = new ArrayList<>();
        given(searchService.searchComments(any(String.class), any(PageRequest.class))).
                willReturn(commentPage);
        given(searchService.searchCommentsByPostTag(
                any(PostTag.class),
                any(String.class),
                any(PageRequest.class)
        )).willReturn(commentPage);

        mvc.perform(get("/comments").
                requestAttr("userId", USER_ID).
                param("searchKey", "WTF").
                param("pageNum", "0").
                param("pageSize", "8")
            ).andExpect(status().isOk());

        mvc.perform(get("/comments").
                requestAttr("userId", USER_ID).
                param("postTag", TAG.toString()).
                param("searchKey", "WTF").
                param("pageNum", "0").
                param("pageSize", "8")
        ).andExpect(status().isOk());
    }

    @Test
    void testSearchUsers() throws Exception {
        // Verify status code and return type.
        mvc.perform(get("/users").
                requestAttr("userId", USER_ID).
                param("searchKey", "wtf?")
        ).andExpect(status().isOk()).andReturn();
    }
}
