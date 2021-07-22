package com.privateboat.forum.backend.dto.request;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class RegisterDTO {
    String email;
    String password;
    @Nullable
    String emailToken;
}
