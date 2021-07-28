package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.StarRecordDAO;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.StarRecord;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.repository.StarRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class StarRecordRepositoryImpl implements StarRecordRepository {
    private final StarRecordDAO starRecordDAO;

    @Override
    public Page<StarRecord> getStarRecords(Long userId, Pageable pageable) {
        Page<StarRecord> starRecords = starRecordDAO.findByToUserIdOrderByTimestampDesc(userId, pageable);
        starRecords.forEach((starRecord) -> {
            starRecord.getPost().setTransientProperties();
        });
        return starRecords;
    }

    @Override
    public void save(StarRecord newStarRecord) {
        starRecordDAO.saveAndFlush(newStarRecord);
    }

    @Override
    public void deleteStarRecord(Long fromUserId, Long postId) {
        starRecordDAO.deleteByFromUserIdAndPostId(fromUserId, postId);
    }

    @Override
    public Boolean checkIfHaveStarred(UserInfo userInfo, Post post) {
        return starRecordDAO.existsByFromUserAndPost(userInfo, post);
    }
}
