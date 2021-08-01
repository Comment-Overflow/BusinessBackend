package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.entity.UserStatistic;

public interface UserStatisticService {
    UserStatistic.NewlyRecord getNewlyRecords(Long userId);
}
