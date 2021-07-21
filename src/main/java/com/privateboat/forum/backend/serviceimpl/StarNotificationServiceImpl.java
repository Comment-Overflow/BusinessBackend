package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.entity.StarNotification;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.StarNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.Optional;

public class StarNotificationServiceImpl implements StarNotificationService {
    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    StarNotificationRepository starNotificationRepository;
    @Autowired
    PostRepository postRepository;

    @Override
    public Page<StarNotification> getStarNotifications(Long userId, Pageable pageable){
        return starNotificationRepository.getStarNotifications(userId, pageable);
    }

    @Override
    public void postStarNotification(Long fromUserId, Long toUserId, Long postId) throws UserInfoException {
        StarNotification newStarNotification = new StarNotification();
        Optional<UserInfo> newUserInfo = userInfoRepository.findByUserId();
        if (newUserInfo.isPresent()){
            newStarNotification.setFromUser(userInfoRepository.findByUserId());
        }
        else throw new UserInfoException(UserInfoException.UserInfoExceptionType.USER_NOT_EXIST);
        newStarNotification.setToUserId(toUserId);
        newStarNotification.setTimestamp(new Timestamp(System.currentTimeMillis()));
    }
}
