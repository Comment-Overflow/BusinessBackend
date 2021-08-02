package com.privateboat.forum.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginDTO {
    private final String email;
    private final String password;
}
