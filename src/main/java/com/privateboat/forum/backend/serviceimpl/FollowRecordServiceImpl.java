package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.entity.FollowRecord;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.FollowRecordRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.FollowRecordService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;

@Transactional
@Service
@AllArgsConstructor
@Transactional
public class FollowRecordServiceImpl implements FollowRecordService {
    private final FollowRecordRepository followRecordRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public Page<FollowRecord> getFollowRecords(Long userId, Pageable pageable) throws UserInfoException {
        Page<FollowRecord> followRecords = followRecordRepository.getFollowRecords(userId, pageable);
        UserInfo userInfo = userInfoRepository.getById(userId);
        followRecords.forEach((followRecord) -> {
            followRecord.setIsMutual(followRecordRepository.isMutualFollowed(followRecord.getFromUser().getId(), userInfo));
        });
        return followRecords;
    }

    @Override
    public void postFollowRecord(Long fromUserId, Long toUserId) throws UserInfoException {
        FollowRecord newFollowRecord = new FollowRecord();
        UserInfo fromUser = userInfoRepository.getById(fromUserId);
        newFollowRecord.setFromUser(fromUser);
        newFollowRecord.setToUserId(toUserId);
        newFollowRecord.setTimestamp(new Timestamp(System.currentTimeMillis()));
        followRecordRepository.postFollowRecord(newFollowRecord);
    }
}
