package com.privateboat.forum.backend.serviceimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privateboat.forum.backend.service.ForumStatisticService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ForumStatisticServiceImpl implements ForumStatisticService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    static private final String clientChannel = "/forum/statistics";

    @Override
    public List<Long> getForumStatistics() {
        // TODO: Get statistics.
        return new ArrayList<>();
    }

    @Override
    @Scheduled(cron="*/5 * * * * *")
    public void pushForumStatistics() throws JsonProcessingException {
        // TODO: Get statistics.
        List<Long> forumStatistics = new ArrayList<>();
        simpMessagingTemplate.convertAndSend(clientChannel, objectMapper.writeValueAsString(forumStatistics));
    }
}
