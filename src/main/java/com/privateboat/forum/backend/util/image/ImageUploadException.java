package com.privateboat.forum.backend.util.image;

public class ImageUploadException extends RuntimeException {
    public enum ExceptionType {
        NETWORK_ERROR, ILLEGAL_CONTENT
    }

    private final ImageUploadException.ExceptionType type;

    public ImageUploadException(ExceptionType type) {
        this.type = type;
    }
}
