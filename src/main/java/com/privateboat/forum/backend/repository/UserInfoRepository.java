package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.UserInfo;

import java.util.Optional;

public interface UserInfoRepository {
    UserInfo save(UserInfo userInfo);
    Optional<UserInfo> findByUserId(Long userId);
}
