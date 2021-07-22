package com.privateboat.forum.backend.dto.response;

import lombok.Data;

@Data
public class LoginDTO {
    private final Long userId;
    private final String token;
}
