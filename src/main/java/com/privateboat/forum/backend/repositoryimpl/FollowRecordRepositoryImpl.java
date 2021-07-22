package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.FollowRecordDAO;
import com.privateboat.forum.backend.entity.FollowRecord;
import com.privateboat.forum.backend.entity.UserInfo;
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
    public Page<FollowRecord> getFollowRecords(Long userId, Pageable pageable) {
        return followRecordDAO.getByToUserId(userId, pageable);
    }

    @Override
    public void postFollowRecord(FollowRecord followRecord) {
        followRecordDAO.saveAndFlush(followRecord);
    }

    @Override
    public Boolean isMutualFollowed(Long userId, UserInfo userInfo){
        return followRecordDAO.existsByToUserIdAndFromUser(userId, userInfo);
    }
}
