package com.privateboat.forum.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ForumStatisticService {
    List<Long> getForumStatistics();
    void pushForumStatistics() throws JsonProcessingException;
}
