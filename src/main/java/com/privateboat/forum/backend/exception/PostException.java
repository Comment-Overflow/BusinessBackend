package com.privateboat.forum.backend.exception;

import java.util.HashMap;
import java.util.Map;

public class PostException extends RuntimeException {
    public enum PostExceptionType {
        POST_NOT_EXIST, POST_DELETED, PAGE_OUT_OF_BOUND, POSTER_NOT_EXIST, UPLOAD_IMAGE_FAILED, COMMENT_NOT_EXIST,
        VIEWER_NOT_EXIST, QUOTE_OUT_OF_BOUND, PERMISSION_DENIED, USER_SILENCED, POST_FROZEN, ILLEGAL_TEXT,
        ILLEGAL_IMAGE, EMPTY_TITLE
    }

    private final PostExceptionType type;

    private static final Map<PostExceptionType, String> map = new HashMap<PostExceptionType, String>() {{
        put(PostExceptionType.COMMENT_NOT_EXIST, "回复不存在！");
        put(PostExceptionType.POST_NOT_EXIST, "帖子不存在！");
        put(PostExceptionType.POST_DELETED, "帖子已经被删除！");
        put(PostExceptionType.PAGE_OUT_OF_BOUND, "浏览帖子页数越界。");
        put(PostExceptionType.POSTER_NOT_EXIST, "发贴用户不存在。");
        put(PostExceptionType.UPLOAD_IMAGE_FAILED, "上传图片失败。");
        put(PostExceptionType.VIEWER_NOT_EXIST, "访问用户不存在。");
        put(PostExceptionType.QUOTE_OUT_OF_BOUND, "引用了不是本楼的回复！");
        put(PostExceptionType.PERMISSION_DENIED, "删除权限不足！");
        put(PostExceptionType.USER_SILENCED, "用户已被禁言！");
        put(PostExceptionType.ILLEGAL_TEXT, "帖子中存在不合规内容");
        put(PostExceptionType.ILLEGAL_IMAGE, "图片中存在不合规内容");
        put(PostExceptionType.POST_FROZEN, "该帖子已被冻结！");
        put(PostExceptionType.EMPTY_TITLE, "标题为空！");
    }};

    public PostException(PostExceptionType postExceptionType) {
        super(map.get(postExceptionType));
        this.type = postExceptionType;
    }

    public PostExceptionType getType() {
        return type;
    }
}
