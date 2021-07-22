package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.UserInfoDAO;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class UserInfoRepositoryImpl implements UserInfoRepository {
    UserInfoDAO userInfoDao;

    @Override
    public UserInfo save(UserInfo userInfo) {
        return userInfoDao.save(userInfo);
    }

    @Override
    public Optional<UserInfo> findByUserId(Long userId) {
        return userInfoDao.findById(userId);
    }

    @Override
    public UserInfo getById(Long userId) throws UserInfoException {
        UserInfo userInfo = userInfoDao.getById(userId);
        if(userInfo.equals(null)){
            return userInfo;
        }
        else throw new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);
    }
}
