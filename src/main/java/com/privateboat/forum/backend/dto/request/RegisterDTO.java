package com.privateboat.forum.backend.dto.request;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class RegisterDTO {
    String email;
    String password;
    String userCode;
    @Nullable
    String emailToken;
}
