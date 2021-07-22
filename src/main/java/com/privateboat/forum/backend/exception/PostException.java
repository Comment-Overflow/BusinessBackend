package com.privateboat.forum.backend.exception;

import java.util.HashMap;
import java.util.Map;

public class PostException extends Exception {
    public enum PostExceptionType {
        POST_NOT_EXIST, POST_DELETED, PAGE_OUT_OF_BOUND, POSTER_NOT_EXIST, UPLOAD_IMAGE_FAILED
    }

    private static final Map<PostExceptionType, String> map = new HashMap<>() {{
        put(PostExceptionType.POST_NOT_EXIST, "帖子不存在！");
        put(PostExceptionType.POST_DELETED, "帖子已经被删除！");
        put(PostExceptionType.PAGE_OUT_OF_BOUND, "浏览帖子页数越界。");
        put(PostExceptionType.POSTER_NOT_EXIST, "发贴用户不存在。");
        put(PostExceptionType.UPLOAD_IMAGE_FAILED, "上传图片失败");
    }};

    public PostException(PostExceptionType postExceptionType) {
        super(map.get(postExceptionType));
    }
}
