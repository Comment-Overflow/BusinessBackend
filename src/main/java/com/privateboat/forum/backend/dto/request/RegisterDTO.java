package com.privateboat.forum.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

@AllArgsConstructor
@Data
public class RegisterDTO {
    String email;
    String password;
    String userCode;
    @Nullable
    String emailToken;
}
