package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.exception.UserInfoException;
import org.apache.catalina.User;

import java.util.List;
import java.util.Optional;

public interface UserInfoRepository {
    UserInfo save(UserInfo userInfo);
    Optional<UserInfo> findByUserId(Long userId);
    UserInfo getById(Long userId) throws UserInfoException;
    List<UserInfo> findByUserNameContainingIgnoreCase(String searchKey);
    UserInfo.UserNameAndAvatarUrl getUserNameAndAvatarUrlById(Long userId);
}
