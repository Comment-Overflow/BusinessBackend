package com.privateboat.forum.backend.dto;

import lombok.Data;

@Data
public class AuthDTO {
    private final String email;
    private final String password;
}
