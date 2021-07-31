package com.privateboat.forum.backend.fakedata;

import com.privateboat.forum.backend.entity.UserAuth;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.entity.UserStatistic;
import com.privateboat.forum.backend.enumerate.Gender;
import com.privateboat.forum.backend.enumerate.UserType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserData {
    static final public String EMAIL = "gungnir_guo@sjtu.edu.cn";
    static final public String DUPLICATE_EMAIL = "guozhidong12@126.com";
    static final public String PASSWORD = "guozhdiong12";
    static final public String WRONG_PASSWORD = "abc";
    static final public String CORRECT_EMAIL_CODE = "123456";
    static final public String WRONG_EMAIL_CODE = "654321";
    static final public String EXPIRED_EMAIL_CODE = "123123";
    static final public String FAKE_TOKEN = "";
    static final public Long USER_ID = 1L;
    static final public UserAuth USER_AUTH = new UserAuth();
    static final public String ENCODED_PASSWORD;
    static final public String WRONG_ENCODED_PASSWORD;

    static final public UserInfo USER_INFO = new UserInfo();
    static final public String USER_NAME = "Gun9niR";
    static final public String BRIEF = "wo shi sha bi";
    static final public Gender GENDER = Gender.SECRET;

    static final public UserStatistic USER_STATISTIC = new UserStatistic();

    static {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        ENCODED_PASSWORD = encoder.encode(PASSWORD);
        WRONG_ENCODED_PASSWORD = encoder.encode(WRONG_PASSWORD);

        USER_AUTH.setUserId(USER_ID);
        USER_AUTH.setEmail(EMAIL);
        USER_AUTH.setPassword(ENCODED_PASSWORD);
        USER_AUTH.setUserType(UserType.USER);

        USER_INFO.setId(USER_ID);
        USER_INFO.setUserStatistic(USER_STATISTIC);
        USER_INFO.setUserAuth(USER_AUTH);
        USER_INFO.setUserName(USER_NAME);
        USER_INFO.setBrief(BRIEF);
        USER_INFO.setAvatarUrl(null);
        USER_INFO.setGender(GENDER);
    }
}
