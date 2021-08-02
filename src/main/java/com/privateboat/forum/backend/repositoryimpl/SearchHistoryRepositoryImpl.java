package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.SearchHistoryDAO;
import com.privateboat.forum.backend.entity.SearchHistory;
import com.privateboat.forum.backend.repository.SearchHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class SearchHistoryRepositoryImpl implements SearchHistoryRepository {

    private final SearchHistoryDAO searchHistoryDAO;

    @Override
    public SearchHistory save(SearchHistory searchHistory) {
        return searchHistoryDAO.save(searchHistory);
    }
}
