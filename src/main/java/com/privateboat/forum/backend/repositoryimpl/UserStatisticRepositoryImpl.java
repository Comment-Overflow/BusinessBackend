package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.UserStatisticDAO;
import com.privateboat.forum.backend.entity.UserStatistic;
import com.privateboat.forum.backend.enumerate.RecordType;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.UserStatisticRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;

@Repository
@AllArgsConstructor
public class UserStatisticRepositoryImpl implements UserStatisticRepository {
    private final UserStatisticDAO userStatisticDAO;

    @Override
    public UserStatistic getByUserId(Long userId) {
        try {
            return userStatisticDAO.getById(userId);
        } catch (EntityNotFoundException e) {
            throw new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);
        }
    }

    @Override
    public void setFlag(Long userId, RecordType recordType) {
        UserStatistic userStatistic;
        try {
            userStatistic = userStatisticDAO.getById(userId);
        } catch (EntityNotFoundException e) {
            throw new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);
        }
        switch (recordType){
            case APPROVAL:
                userStatistic.setIsNewlyApproved(true);
                break;
            case STAR:
                userStatistic.setIsNewlyStarred(true);
                break;
            case FOLLOW:
                userStatistic.setIsNewlyFollowed(true);
                break;
            case REPLY:
                userStatistic.setIsNewlyReplied(true);
                break;
        }
        userStatisticDAO.save(userStatistic);
    }

    @Override
    public void save(UserStatistic userStatistic){
        userStatisticDAO.saveAndFlush(userStatistic);
    }

    @Override
    public void removeFlag(Long userId, RecordType recordType) throws UserInfoException {
        UserStatistic userStatistic;
        try {
             userStatistic = userStatisticDAO.getById(userId);
        } catch (EntityNotFoundException e) {
            throw new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);
        }
        switch (recordType){
            case APPROVAL:
                userStatistic.setIsNewlyApproved(false);
                break;
            case STAR:
                userStatistic.setIsNewlyStarred(false);
                break;
            case REPLY:
                userStatistic.setIsNewlyReplied(false);
                break;
            case FOLLOW:
                userStatistic.setIsNewlyFollowed(false);
                break;
        }
        userStatisticDAO.save(userStatistic);
    }
}
