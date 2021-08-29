package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.UserInfoDAO;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Repository
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
        try {
            return userInfoDao.getById(userId);
        } catch (EntityNotFoundException e) {
            throw new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);
        }
    }

    @Override
    public List<UserInfo> findByUserNameContainingIgnoreCase(String searchKey) {
        return userInfoDao.findByUserNameContainingIgnoreCase(searchKey);
    }

    @Override
    public UserInfo.UserNameAndAvatarUrl getUserNameAndAvatarUrlById(Long userId) {
        Optional<UserInfo.UserNameAndAvatarUrl> userNameAndAvatarUrl =
                userInfoDao.findOneProjectionById(userId, UserInfo.UserNameAndAvatarUrl.class);
        if (!userNameAndAvatarUrl.isPresent())
            throw new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);
        return userNameAndAvatarUrl.get();
    }
}
