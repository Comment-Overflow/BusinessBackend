package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.UserAuth;

public interface UserAuthRepository {
    Boolean existsByEmail(String email);
}
