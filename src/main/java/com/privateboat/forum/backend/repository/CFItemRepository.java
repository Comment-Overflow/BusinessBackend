package com.privateboat.forum.backend.repository;

import java.util.List;

public interface CFItemRepository {
    List<Long> getCFItemByUserId(Long userId);
    void saveOneUserCFItem(Long userId, List<Long> postIdList);
}
