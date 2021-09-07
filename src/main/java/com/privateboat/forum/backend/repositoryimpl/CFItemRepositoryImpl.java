package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.CFItemDAO;
import com.privateboat.forum.backend.entity.CFItem;
import com.privateboat.forum.backend.repository.CFItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class CFItemRepositoryImpl implements CFItemRepository {
    private final CFItemDAO cfItemDAO;

    @Override
    public List<Long> getCFItemByUserId(Long userId) {
        return cfItemDAO.getByUserId(userId);
    }

    @Override
    public void saveOneUserCFItem(Long userId, List<Long> postIdList) {
        List<CFItem> cfItemList = postIdList.stream().map(e -> new CFItem(userId, e)).collect(Collectors.toList());
        cfItemDAO.saveAll(cfItemList);
    }
}
