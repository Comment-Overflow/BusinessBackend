package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.StarRecordDAO;
import com.privateboat.forum.backend.entity.StarRecord;
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
        return starRecordDAO.findByToUserId(userId, pageable);
    }

    @Override
    public void postStarRecord(StarRecord newStarRecord) {
        starRecordDAO.saveAndFlush(newStarRecord);
    }
}
