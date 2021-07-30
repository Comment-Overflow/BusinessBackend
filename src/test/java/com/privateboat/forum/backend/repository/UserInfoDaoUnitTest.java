package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.dao.UserInfoDAO;
import com.privateboat.forum.backend.entity.UserAuth;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.entity.UserStatistic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.privateboat.forum.backend.fakedata.UserData.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserInfoDaoUnitTest {

    @Autowired
    private UserInfoDAO userInfoDAO;

    @Test
    void testSave() {
        UserInfo savedUserInfo = userInfoDAO.save(USER_INFO);
        assertSame(savedUserInfo.getUserName(), USER_INFO.getUserName());
        assertSame(savedUserInfo.getAvatarUrl(), USER_INFO.getAvatarUrl());
        assertSame(savedUserInfo.getId(), USER_INFO.getId());
        assertEquals(savedUserInfo.getBrief(), USER_INFO.getBrief());
        assertSame(savedUserInfo.getGender(), USER_INFO.getGender());

        UserStatistic userStatistic = savedUserInfo.getUserStatistic();
        assertSame(userStatistic.getApprovalCount(), USER_STATISTIC.getApprovalCount());
        assertSame(userStatistic.getCommentCount(), USER_STATISTIC.getCommentCount());
        assertSame(userStatistic.getFollowerCount(), USER_STATISTIC.getFollowerCount());
        assertSame(userStatistic.getFollowingCount(), USER_STATISTIC.getFollowingCount());
        assertSame(userStatistic.getIsNewlyApproved(), USER_STATISTIC.getIsNewlyApproved());
        assertSame(userStatistic.getIsNewlyFollowed(), USER_STATISTIC.getIsNewlyFollowed());
        assertSame(userStatistic.getIsNewlyReplied(), USER_STATISTIC.getIsNewlyReplied());
        assertSame(userStatistic.getIsNewlyStarred(), USER_STATISTIC.getIsNewlyStarred());

        UserAuth userAuth = savedUserInfo.getUserAuth();
        assertSame(userAuth.getUserType(), USER_AUTH.getUserType());
        assertEquals(userAuth.getEmail(), USER_AUTH.getEmail());
    }

    @Test
    void testFindById() {
        userInfoDAO.save(USER_INFO);
        assertSame(userInfoDAO.findById(USER_ID).get().getId(), USER_ID);
        assertFalse(userInfoDAO.findById(WRONG_USER_ID).isPresent());
    }

    @Test
    void testGetById() {
        userInfoDAO.save(USER_INFO);
        assertSame(userInfoDAO.getById(USER_ID).getId(), USER_ID);
        // Must print to throw this exception.
        assertThrows(Exception.class, () -> System.out.println(userInfoDAO.getById(WRONG_USER_ID)));
    }
}
