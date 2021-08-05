package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.service.RecommendService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;

    @GetMapping(value = "/recommendations")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<List<Post>> getRecommendations(@RequestParam Long userId) {
        try {
            return ResponseEntity.ok(recommendService.getCBRecommendations(userId));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
