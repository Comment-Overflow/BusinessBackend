package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchHistoryDAO extends JpaRepository<SearchHistory, Long> {
}
