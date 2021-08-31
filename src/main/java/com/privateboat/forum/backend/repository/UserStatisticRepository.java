package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.UserStatistic;
import com.privateboat.forum.backend.enumerate.RecordType;

public interface UserStatisticRepository {
    UserStatistic getByUserId(Long userId);
    void setFlag(Long userId, RecordType recordType);
    UserStatistic save(UserStatistic userStatistic);
    void removeFlag(Long userId, RecordType recordType);
    UserStatistic.NewRecord getNewlyRecordByUserId(Long userId);
    void addApprovalCount(Long userId);
    void subApprovalCount(Long userId);
}
