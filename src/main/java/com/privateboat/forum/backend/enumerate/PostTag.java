package com.privateboat.forum.backend.enumerate;

import java.util.HashMap;
import java.util.Map;

public enum PostTag {
    LIFE, STUDY, ART, MOOD, CAREER;

    private static final Map<String, PostTag> map = new HashMap<>() {{
        put("PostTag.Life", LIFE);
        put("PostTag.Study", STUDY);
        put("PostTag.Art", ART);
        put("PostTag.Mood", MOOD);
        put("PostTag.Career", CAREER);
    }};

    public static PostTag toPostTag(String tagString) {
        return map.get(tagString);
    }
}
