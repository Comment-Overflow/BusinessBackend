package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.FollowRecordDAO;
import com.privateboat.forum.backend.entity.FollowRecord;
import com.privateboat.forum.backend.enumerate.FollowStatus;
import com.privateboat.forum.backend.repository.FollowRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class FollowRecordRepositoryImpl implements FollowRecordRepository {
    FollowRecordDAO followRecordDAO;

    @Override
    public Page<FollowRecord> getFollowingRecords(Long userId, Pageable pageable) {
        return followRecordDAO.getByToUserId(userId, pageable);
    }

    @Override
    public Page<FollowRecord> getFollowedRecords(Long userId, Pageable pageable) {
        return followRecordDAO.getByFromUserId(userId, pageable);
    }

    @Override
    public void save(FollowRecord followRecord) {
        followRecordDAO.saveAndFlush(followRecord);
    }

    @Override
    public FollowStatus getFollowStatus(Long fromUserId, Long toUserId) {//usually `me` is fromUserId
        Boolean meFollowing = followRecordDAO.existsByFromUserIdAndToUserId(fromUserId, toUserId);
        Boolean meFollowed = followRecordDAO.existsByFromUserIdAndToUserId(toUserId, fromUserId);;
        if (meFollowed && meFollowing) {
            return FollowStatus.BOTH;
        } else if (!meFollowed && !meFollowing) {
            return FollowStatus.NONE;
        } else if (meFollowed) {
            return FollowStatus.FOLLOWED_BY_ME;
        } else {
            return FollowStatus.FOLLOWING_ME;
        }
    }

    @Override
    public void deleteFollowRecord(Long fromUserId, Long toUserId) {
        followRecordDAO.deleteByFromUserIdAndToUserId(fromUserId, toUserId);
    }

}
