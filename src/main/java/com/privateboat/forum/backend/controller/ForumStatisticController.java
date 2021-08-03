package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.service.ForumStatisticService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ForumStatisticController {

    private final ForumStatisticService forumStatisticService;

    @GetMapping(value = "/forum/statistics")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    public ResponseEntity<?> getAppStatistics() {
        try {
            return ResponseEntity.ok(forumStatisticService.getForumStatistics());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
