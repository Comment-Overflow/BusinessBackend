package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.UserAuth;

import java.util.Optional;

public interface UserAuthRepository {
    Boolean existsByEmail(String email);

    Optional<UserAuth> findByEmail(String email);

    UserAuth getByUserId(Long userId);

    UserAuth save(UserAuth userAuth);
}
