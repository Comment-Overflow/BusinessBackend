package com.privateboat.forum.backend.exception;

import java.util.HashMap;
import java.util.Map;

public class ProfileException extends RuntimeException{
    public enum ProfileExceptionType {
        UPLOAD_IMAGE_FAILED, GENDER_NOT_VALID, ILLEGAL_USER_NAME, ILLEGAL_BRIEF, ILLEGAL_AVATAR
    }

    private final ProfileExceptionType type;

    private static final Map<ProfileExceptionType, String> map = new HashMap<>(){{
        put(ProfileExceptionType.UPLOAD_IMAGE_FAILED, "图片上传失败");
        put(ProfileExceptionType.GENDER_NOT_VALID, "性别有误");
        put(ProfileExceptionType.ILLEGAL_USER_NAME, "用户名包含不合法内容");
        put(ProfileExceptionType.ILLEGAL_BRIEF, "用户简介包含不合法内容");
        put(ProfileExceptionType.ILLEGAL_AVATAR, "头像包含不合法内容");

    }};

    public ProfileException(ProfileExceptionType profileExceptionType) {
        super(map.get(profileExceptionType));
        type = profileExceptionType;
    }

    public ProfileExceptionType getType() {
        return type;
    }
}
