package com.privateboat.forum.backend.dto.response;

import lombok.Data;

@Data
public class ProfileDTO {
    private String userName;
    private String brief;
    private String avatarUrl;
    private String gender;
}
