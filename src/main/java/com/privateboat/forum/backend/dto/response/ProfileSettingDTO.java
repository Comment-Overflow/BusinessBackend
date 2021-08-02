package com.privateboat.forum.backend.dto.response;

import lombok.Data;

@Data
public class ProfileSettingDTO {
    private String userName;
    private String brief;
    private String avatarUrl;
    private String gender;
}
