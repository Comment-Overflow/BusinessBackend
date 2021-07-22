package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.StarRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StarRecordRepository {
    Page<StarRecord> getStarRecords(Long userId, Pageable pageable);

    void postStarRecord(StarRecord starRecord);
}
