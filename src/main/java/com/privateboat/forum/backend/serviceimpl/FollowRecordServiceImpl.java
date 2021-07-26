package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.entity.FollowRecord;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.FollowStatus;
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
public class FollowRecordServiceImpl implements FollowRecordService {
    private final FollowRecordRepository followRecordRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public Page<FollowRecord> getFollowRecords(Long userId, Pageable pageable) throws UserInfoException {
        Page<FollowRecord> followRecords = followRecordRepository.getFollowRecords(userId, pageable);
        followRecords.forEach((followRecord) -> {
            followRecord.setIsMutual(followRecordRepository.isFollowing(userId, followRecord.getFromUser().getId()));
        });
        return followRecords;
    }

    @Override
    public FollowStatus getFollowStatus(Long fromUserId, Long toUserId) {
        Boolean meFollowing = followRecordRepository.isFollowing(fromUserId, toUserId);
        Boolean meFollowed = followRecordRepository.isFollowing(fromUserId, toUserId);

        if (meFollowed && meFollowing) {
            return FollowStatus.BOTH;
        } else if (!meFollowed && !meFollowing) {
            return FollowStatus.NONE;
        } else if (meFollowed) {
            return FollowStatus.FOLLOWING_ME;
        } else {
            return FollowStatus.FOLLOWED_BY_ME;
        }
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
