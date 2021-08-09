package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.exception.AdminException;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.interceptor.JWTInterceptor;
import com.privateboat.forum.backend.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
public class AdminControllerUnitTest {
    static final private Long ADMIN_ID = 114L;
    static final private Long USER_ID = 514L;
    static final private Long POST_ID = 15665L;
    static final private Long WRONG_ADMIN_ID = 1919L;
    static final private Long WRONG_USER_ID = 810L;
    static final private Long WRONG_POST_ID = 525L;


    @Autowired
    private MockMvc mvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    JWTInterceptor jwtInterceptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(jwtInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        Mockito.doThrow(new AdminException(AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN))
                .when(adminService)
                .silenceUser(WRONG_ADMIN_ID, USER_ID);
        Mockito.doThrow(new AdminException(AdminException.AdminExceptionType.INVALID_SILENCE_TARGET))
                .when(adminService)
                .silenceUser(ADMIN_ID, WRONG_USER_ID);

        Mockito.doThrow(new AdminException(AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN))
                .when(adminService)
                .freeUser(WRONG_ADMIN_ID, USER_ID);
        Mockito.doThrow(new AdminException(AdminException.AdminExceptionType.INVALID_FREE_TARGET))
                .when(adminService)
                .freeUser(ADMIN_ID, WRONG_USER_ID);

        Mockito.doThrow(new AdminException(AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN))
                .when(adminService)
                .freezePost(WRONG_ADMIN_ID, POST_ID);
        Mockito.doThrow(new PostException(PostException.PostExceptionType.POST_NOT_EXIST))
                .when(adminService)
                .freezePost(ADMIN_ID, WRONG_POST_ID);

        Mockito.doThrow(new AdminException(AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN))
                .when(adminService)
                .releasePost(WRONG_ADMIN_ID, POST_ID);
        Mockito.doThrow(new PostException(PostException.PostExceptionType.POST_NOT_EXIST))
                .when(adminService)
                .releasePost(ADMIN_ID, WRONG_POST_ID);
    }

    @Test
    void testSilenceUser() throws Exception {
        mvc.perform(put("/silence/" + USER_ID.toString())
                .requestAttr("userId", ADMIN_ID))
                .andExpect(status().isOk());

        mvc.perform(put("/silence/" + USER_ID.toString())
                        .requestAttr("userId", WRONG_ADMIN_ID))
                .andExpect(status().isConflict());

        mvc.perform(put("/silence/" + WRONG_USER_ID.toString())
                        .requestAttr("userId", ADMIN_ID))
                .andExpect(status().isConflict());
    }

    @Test
    void testFreeUser() throws Exception {
        mvc.perform(put("/freedom/" + USER_ID.toString())
                        .requestAttr("userId", ADMIN_ID))
                .andExpect(status().isOk());

        mvc.perform(put("/freedom/" + USER_ID.toString())
                        .requestAttr("userId", WRONG_ADMIN_ID))
                .andExpect(status().isConflict());

        mvc.perform(put("/freedom/" + WRONG_USER_ID.toString())
                        .requestAttr("userId", ADMIN_ID))
                .andExpect(status().isConflict());
    }

    @Test
    void testFreezePost() throws Exception {
        mvc.perform(put("/freeze/" + POST_ID.toString())
                        .requestAttr("userId", ADMIN_ID))
                .andExpect(status().isOk());

        mvc.perform(put("/freeze/" + POST_ID.toString())
                        .requestAttr("userId", WRONG_ADMIN_ID))
                .andExpect(status().isConflict());

        mvc.perform(put("/freeze/" + WRONG_POST_ID.toString())
                        .requestAttr("userId", ADMIN_ID))
                .andExpect(status().isConflict());
    }

    @Test
    void testReleasePost() throws Exception {
        mvc.perform(put("/release/" + POST_ID.toString())
                        .requestAttr("userId", ADMIN_ID))
                .andExpect(status().isOk());

        mvc.perform(put("/release/" + POST_ID.toString())
                        .requestAttr("userId", WRONG_ADMIN_ID))
                .andExpect(status().isConflict());

        mvc.perform(put("/release/" + WRONG_POST_ID.toString())
                        .requestAttr("userId", ADMIN_ID))
                .andExpect(status().isConflict());
    }
}
