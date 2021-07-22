package com.privateboat.forum.backend.dto.recordback;

import lombok.Data;

@Data
public class StarRecordDTO {
    private Long timestamp;

    private String username;
    private String avatarUrl;

    private String postTitle;
    private String postContent;
}
