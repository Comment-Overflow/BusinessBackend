package com.privateboat.forum.backend.repository;

import com.privateboat.forum.backend.entity.SearchHistory;

public interface SearchHistoryRepository {
    SearchHistory save(SearchHistory searchHistory);
}
