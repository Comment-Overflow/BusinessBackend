package com.privateboat.forum.backend.fakedata;

import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.PostTag;
import com.privateboat.forum.backend.enumerate.PreferenceDegree;

import java.util.HashMap;
import java.util.Optional;

public class Recommend {
    public static final Long VALID_USER_ID = 1L;
    public static final int PAGE_OFFSET = 1;
    public static final int PAGE_SIZE = 1;
    public static final Long POST_ID = 1L;
    public static final PreferenceDegree DEGREE = PreferenceDegree.BROWSE;

    public static final HashMap<PostTag, HashMap<String, Long>> PREFERRED_WORD_MAP = new HashMap<>(){{
        put(PostTag.ART, new HashMap<>(){{put("key1", 1L);}});
        put(PostTag.CAREER, new HashMap<>(){{put("key2", 2L);}});
    }};
}
