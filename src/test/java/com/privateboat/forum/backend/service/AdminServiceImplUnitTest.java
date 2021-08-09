package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.UserAuth;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.enumerate.UserType;
import com.privateboat.forum.backend.exception.AdminException;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.repository.PostRepository;
import com.privateboat.forum.backend.repository.UserAuthRepository;
import com.privateboat.forum.backend.serviceimpl.AdminServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;

public class AdminServiceImplUnitTest {
    static private final Long ADMIN_ID = 114L;
    static private final Long NULL_ID = 514L;
    static private final Long USER_ID = 1919L;
    static private final Long SILENCE_ID = 810L;
    static private final Long POST_ID = 4234L;
    static private final Long WRONG_POST_ID = 165161L;

    static final private String TITLE = "TITLE";
    static final private PostTag TAG = PostTag.LIFE;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        UserAuth ADMIN_AUTH = new UserAuth();
        ADMIN_AUTH.setUserType(UserType.ADMIN);
        UserAuth USER_AUTH = new UserAuth();
        USER_AUTH.setUserType(UserType.USER);
        UserAuth SILENCE_AUTH = new UserAuth();
        SILENCE_AUTH.setUserType(UserType.SILENCED);

        Post post = new Post(TITLE, TAG);
        post.setIsFrozen(false);

        Mockito.when(userAuthRepository.getByUserId(ADMIN_ID))
                .thenReturn(ADMIN_AUTH);

        Mockito.when(userAuthRepository.getByUserId(USER_ID))
                .thenReturn(USER_AUTH);

        Mockito.when(userAuthRepository.getByUserId(SILENCE_ID))
                .thenReturn(SILENCE_AUTH);

        Mockito.when(postRepository.getByPostId(POST_ID))
                .thenReturn(post);

        Mockito.when(userAuthRepository.getByUserId(NULL_ID))
                .thenReturn(null);

        Mockito.when(postRepository.getByPostId(WRONG_POST_ID))
                .thenReturn(null);
    }

    @Test
    void testSilenceUser() {
        try {
            adminService.silenceUser(ADMIN_ID, USER_ID);
            Mockito.verify(userAuthRepository).save(any());
        } catch (Exception e) {
            Assertions.assertNull(e);
        }

        AdminException authException =
                Assertions.assertThrows(AdminException.class, () -> adminService
                        .silenceUser(USER_ID, USER_ID));
        Assertions.assertSame(authException.getType(),
                AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN);

        AdminException authNullException =
                Assertions.assertThrows(AdminException.class, () -> adminService
                        .silenceUser(NULL_ID, USER_ID));
        Assertions.assertSame(authNullException.getType(),
                AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN);

        AdminException targetException =
                Assertions.assertThrows(AdminException.class, () -> adminService
                        .silenceUser(ADMIN_ID, SILENCE_ID));
        Assertions.assertSame(targetException.getType(),
                AdminException.AdminExceptionType.INVALID_SILENCE_TARGET);
    }

    @Test
    void testFreeUser() {
        try {
            adminService.freeUser(ADMIN_ID, SILENCE_ID);
            Mockito.verify(userAuthRepository).save(any());
        } catch (Exception e) {
            Assertions.assertNull(e);
        }

        AdminException authException =
                Assertions.assertThrows(AdminException.class, () -> adminService
                        .freeUser(USER_ID, SILENCE_ID));
        Assertions.assertSame(authException.getType(),
                AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN);

        AdminException authNullException =
                Assertions.assertThrows(AdminException.class, () -> adminService
                        .freeUser(NULL_ID, SILENCE_ID));
        Assertions.assertSame(authNullException.getType(),
                AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN);

        AdminException targetException =
                Assertions.assertThrows(AdminException.class, () -> adminService
                        .freeUser(ADMIN_ID, USER_ID));
        Assertions.assertSame(targetException.getType(),
                AdminException.AdminExceptionType.INVALID_FREE_TARGET);
    }

    @Test
    void testFreezePost() {
        try {
            adminService.freezePost(ADMIN_ID, POST_ID);
            Mockito.verify(postRepository).save(any());
        } catch (Exception e) {
            Assertions.assertNull(e);
        }

        AdminException authException =
                Assertions.assertThrows(AdminException.class, () -> adminService
                        .freezePost(USER_ID, POST_ID));
        Assertions.assertSame(authException.getType(),
                AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN);

        AdminException authNullException =
                Assertions.assertThrows(AdminException.class, () -> adminService
                        .freezePost(NULL_ID, POST_ID));
        Assertions.assertSame(authNullException.getType(),
                AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN);

        PostException nullException =
                Assertions.assertThrows(PostException.class, () -> adminService
                        .freezePost(ADMIN_ID, WRONG_POST_ID));
        Assertions.assertSame(nullException.getType(),
                PostException.PostExceptionType.POST_NOT_EXIST);
    }

    @Test
    void testReleasePost() {
        try {
            adminService.releasePost(ADMIN_ID, POST_ID);
            Mockito.verify(postRepository).save(any());
        } catch (Exception e) {
            Assertions.assertNull(e);
        }

        AdminException authException =
                Assertions.assertThrows(AdminException.class, () -> adminService
                        .releasePost(USER_ID, POST_ID));
        Assertions.assertSame(authException.getType(),
                AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN);

        AdminException authNullException =
                Assertions.assertThrows(AdminException.class, () -> adminService
                        .releasePost(NULL_ID, POST_ID));
        Assertions.assertSame(authNullException.getType(),
                AdminException.AdminExceptionType.OPERATOR_NOT_ADMIN);

        PostException nullException =
                Assertions.assertThrows(PostException.class, () -> adminService
                        .releasePost(ADMIN_ID, WRONG_POST_ID));
        Assertions.assertSame(nullException.getType(),
                PostException.PostExceptionType.POST_NOT_EXIST);
    }
}
