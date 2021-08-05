package com.privateboat.forum.backend.util.image;

import lombok.Getter;

@Getter
public class ImageAuditException extends ImageUploadException {
    private final ImageAuditResult result;

    public ImageAuditException(ImageAuditResult result) {
        super(ExceptionType.ILLEGAL_CONTENT);
        this.result = result;
    }
}
