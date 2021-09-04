package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.FollowRecordDAO;
import com.privateboat.forum.backend.entity.FollowRecord;
import com.privateboat.forum.backend.enumerate.FollowStatus;
import com.privateboat.forum.backend.repository.FollowRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
        Boolean followedByMe = followRecordDAO.existsByFromUserIdAndToUserId(fromUserId, toUserId);//我关注
        Boolean followingMe = followRecordDAO.existsByFromUserIdAndToUserId(toUserId, fromUserId);//关注我
        if (followedByMe && followingMe) {
            return FollowStatus.BOTH;
        } else if (!followedByMe && !followingMe) {
            return FollowStatus.NONE;
        } else if (followedByMe) {
            return FollowStatus.FOLLOWED_BY_ME;
        } else {
            return FollowStatus.FOLLOWING_ME;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteFollowRecord(Long fromUserId, Long toUserId) {
        followRecordDAO.deleteByFromUserIdAndToUserId(fromUserId, toUserId);
    }

}
