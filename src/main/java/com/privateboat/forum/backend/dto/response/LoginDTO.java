package com.privateboat.forum.backend.dto.response;

import com.privateboat.forum.backend.enumerate.UserType;
import lombok.Data;

@Data
public class LoginDTO {
    private final String token;
    private final Long userId;
    private final String userName;
    private final String avatarUrl;
    private final UserType userType;
}
