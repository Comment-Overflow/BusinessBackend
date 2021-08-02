package com.privateboat.forum.backend.exception;

import java.util.HashMap;
import java.util.Map;

public class ProfileException extends RuntimeException{
    public enum ProfileExceptionType {
        UPLOAD_IMAGE_FAILED, GENDER_NOT_VALID
    }

    private ProfileExceptionType type;

    private static final Map<ProfileExceptionType, String> map = new HashMap<>(){{
        put(ProfileExceptionType.UPLOAD_IMAGE_FAILED, "图片上传失败");
        put(ProfileExceptionType.GENDER_NOT_VALID, "性别有误");
    }};

    public ProfileException(ProfileExceptionType profileExceptionType) {
        super(map.get(profileExceptionType));
        type = profileExceptionType;
    }

    public ProfileExceptionType getType() {
        return type;
    }
}
