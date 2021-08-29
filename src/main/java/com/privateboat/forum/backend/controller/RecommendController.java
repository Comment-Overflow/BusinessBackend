package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.service.RecommendService;
import com.privateboat.forum.backend.util.JWTUtil;
import com.privateboat.forum.backend.util.LogUtil;
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

    @GetMapping(value = "/recommendations/content_based")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<Post>> getCBRecommendations(@RequestAttribute Long userId,
                                                    @RequestParam("pageNum") Integer page,
                                                    @RequestParam("pageSize") Integer pageSize) {
        try {
            return ResponseEntity.ok(recommendService.getCBRecommendations(userId));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/recommendations/collaborative_filter")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<Post>> getCFRecommendations(@RequestAttribute Long userId,
                                                    @RequestParam("pageNum") Integer page,
                                                    @RequestParam("pageSize") Integer pageSize
    ) {
        try {
            return ResponseEntity.ok(recommendService.getCFRecommendations(userId));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/recommendations/all")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<Post>> getAllRecommendations(@RequestAttribute Long userId,
                                                     @RequestParam("pageNum") Integer page,
                                                     @RequestParam("pageSize") Integer pageSize
    ) {
        try {
            List<Post> CBRecommendList = recommendService.getCBRecommendations(userId);
            LogUtil.debug(CBRecommendList.toString());
            LogUtil.debug(CBRecommendList.size());
            List<Post> CFRecommendList = recommendService.getCFRecommendations(userId);
            LogUtil.debug(CFRecommendList.toString());
            LogUtil.debug(CFRecommendList.size());
            CBRecommendList.addAll(CFRecommendList);
            return ResponseEntity.ok(CBRecommendList);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
