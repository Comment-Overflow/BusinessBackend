package com.privateboat.forum.backend.util;


public class Constant {
    public static final Integer EMAIL_EXPIRE_MINUTES = 5;

    public static final String SECRET_ID = "AKIDLVP3QWLtBDzVkiPpyrUqvjGWWEfpiLhm";
    public static final String SECRET_KEY = "qQxEEwOZS5RWKsABuMXS6TZPyFFzSeZD";

    public static final String IMAGE_STRING = "[图片]";

    // week as unit
    public static final Integer RECOMMEND_EXPIRED_TIME = -2;
    public static final Integer CB_RECOMMEND_POST_NUMBER = 10;
    public static final Integer CF_RECOMMEND_POST_NUMBER = 5;
    public static final Integer POST_KEYS_WORDS = 10;
    public static final Integer NEAREST_N_USER = 4;
    public static final Integer POST_CONTENT_MAX_LENGTH = 300;
    public static final String REDIS_HOT_LIST_KEY = "hot-post-list";
}
