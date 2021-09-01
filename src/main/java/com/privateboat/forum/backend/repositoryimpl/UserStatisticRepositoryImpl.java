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
    public UserStatistic.NewRecord getNewlyRecordByUserId(Long userId) {
        try {
            return userStatisticDAO.getNewlyRecordByUserId(userId);
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
    public UserStatistic save(UserStatistic userStatistic){
        return userStatisticDAO.saveAndFlush(userStatistic);
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

    @Override
    public void addApprovalCount(Long userId) {
        userStatisticDAO.incrementApprovalCountByUserId(userId);
    }

    @Override
    public void subApprovalCount(Long userId) {
        userStatisticDAO.decrementApprovalCountByUserId(userId);
    }

    @Override
    public void addCommentCount(Long userId) {
        userStatisticDAO.incrementCommentCountByUserId(userId);
    }

    @Override
    public void subCommentCount(Long userId) {
        userStatisticDAO.decrementCommentCountByUserId(userId);
    }

    @Override
    public void addFollowing(Long userId) {
        userStatisticDAO.incrementFollowingCountByUserId(userId);
    }

    @Override
    public void subFollowing(Long userId) {
        userStatisticDAO.decrementFollowingCountByUserId(userId);
    }

    @Override
    public void addFollower(Long userId) {
        userStatisticDAO.incrementFollowerCountByUserId(userId);
    }

    @Override
    public void subFollower(Long userId) {
        userStatisticDAO.decrementFollowerCountByUserId(userId);
    }

    @Override
    public void addPost(Long userId) {
        userStatisticDAO.incrementPostCountByUserId(userId);
    }

    @Override
    public void subPost(Long userId) {
        userStatisticDAO.decrementPostCountByUserId(userId);
    }
}
