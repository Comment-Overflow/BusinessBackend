package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.UserInfo;

public interface UserInfoRepository {
    UserInfo save(UserInfo userInfo);
}
