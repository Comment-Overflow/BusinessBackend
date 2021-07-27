package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.UserStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatisticDAO extends JpaRepository<UserStatistic, Long> {
}
