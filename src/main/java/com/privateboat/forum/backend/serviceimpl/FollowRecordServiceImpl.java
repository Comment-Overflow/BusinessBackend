package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.response.UserCardInfoDTO;
import com.privateboat.forum.backend.entity.FollowRecord;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.RecordType;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.FollowRecordRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.repository.UserStatisticRepository;
import com.privateboat.forum.backend.service.FollowRecordService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;

@Service
@AllArgsConstructor
@Transactional
public class FollowRecordServiceImpl implements FollowRecordService {
    private final FollowRecordRepository followRecordRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserStatisticRepository userStatisticRepository;

    private UserCardInfoDTO converter(FollowRecord followRecord) {
        UserInfo userInfo = followRecord.getFromUser();
        return new UserCardInfoDTO(
                userInfo.getId(),
                userInfo.getUserName(),
                userInfo.getAvatarUrl(),
                userInfo.getBrief(),
                userInfo.getUserStatistic().getCommentCount(),
                userInfo.getUserStatistic().getFollowerCount(),
                followRecord.getFollowStatus()
        );
    }

    @Override
    public Page<FollowRecord> getFollowingNotifications(Long userId, Pageable pageable) throws UserInfoException {
        userStatisticRepository.removeFlag(userId, RecordType.FOLLOW);
        Page<FollowRecord> followRecords = followRecordRepository.getFollowingRecords(userId, pageable);
        followRecords.forEach((followRecord) -> {
            followRecord.setFollowStatus(followRecordRepository.getFollowStatus(userId, followRecord.getFromUser().getId()));
        });
        return followRecords;
    }

    @Override
    public Page<UserCardInfoDTO> getFollowingRecords(Long userId, Pageable pageable) throws UserInfoException {
        Page<FollowRecord> followRecords = followRecordRepository.getFollowingRecords(userId, pageable);
        followRecords.forEach((followRecord) -> {
            followRecord.setFollowStatus(followRecordRepository.getFollowStatus(userId, followRecord.getFromUser().getId()));
        });
        return followRecords.map((followRecord -> {
            UserInfo userInfo = followRecord.getFromUser();
            return new UserCardInfoDTO(
                    userInfo.getId(),
                    userInfo.getUserName(),
                    userInfo.getAvatarUrl(),
                    userInfo.getBrief(),
                    userInfo.getUserStatistic().getCommentCount(),
                    userInfo.getUserStatistic().getFollowerCount(),
                    followRecord.getFollowStatus()
            );
        }));
    }

    @Override
    public Page<UserCardInfoDTO> getFollowedRecords(Long userId, Pageable pageable) throws UserInfoException {
        Page<FollowRecord> followRecords = followRecordRepository.getFollowedRecords(userId, pageable);
        followRecords.forEach((followRecord) -> {
            followRecord.setFollowStatus(followRecordRepository.getFollowStatus(userId, followRecord.getToUserId()));
        });
        return followRecords.map((followRecord -> {
            UserInfo userInfo = userInfoRepository.getById(followRecord.getToUserId());
            return new UserCardInfoDTO(
                    userInfo.getId(),
                    userInfo.getUserName(),
                    userInfo.getAvatarUrl(),
                    userInfo.getBrief(),
                    userInfo.getUserStatistic().getCommentCount(),
                    userInfo.getUserStatistic().getFollowerCount(),
                    followRecord.getFollowStatus()
            );
        }));
    }

    @Override
    public void postFollowRecord(Long fromUserId, Long toUserId) throws UserInfoException {
        FollowRecord newFollowRecord = new FollowRecord();

        UserInfo fromUser = userInfoRepository.getById(fromUserId);
        newFollowRecord.setFromUser(fromUser);

        newFollowRecord.setToUserId(toUserId);

        newFollowRecord.setTimestamp(new Timestamp(System.currentTimeMillis()));

        userStatisticRepository.getByUserId(fromUserId).addFollowing();
        userStatisticRepository.getByUserId(toUserId).addFollower();
        userStatisticRepository.setFlag(toUserId, RecordType.FOLLOW);

        followRecordRepository.save(newFollowRecord);
    }

    @Override
    public void deleteFollowRecord(Long fromUserId, Long toUserId) {
        followRecordRepository.deleteFollowRecord(fromUserId, toUserId);
    }
}
