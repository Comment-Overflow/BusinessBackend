package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.UserInfoDAO;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserInfoRepositoryImpl implements UserInfoRepository {
    UserInfoDAO userInfoDao;

    @Override
    public UserInfo save(UserInfo userInfo) {
        return userInfoDao.save(userInfo);
    }
}
