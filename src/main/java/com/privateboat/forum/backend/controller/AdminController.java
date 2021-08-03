package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.service.AdminService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AdminController {
    final AdminService adminService;

    @PutMapping(value = "/silence/{silenceUserId}")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<?> silenceUser(@RequestAttribute Long userId,
                                  @PathVariable Long silenceUserId) {
        try {
            adminService.silenceUser(userId, silenceUserId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping(value = "/freedom/{freeUserId}")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<?> freeUser(@RequestAttribute Long userId,
                               @PathVariable Long freeUserId) {
        try {
            adminService.freeUser(userId, freeUserId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping(value = "/freeze/{freezePostId}")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<?> freezePost(@RequestAttribute Long userId,
                                 @PathVariable Long freezePostId) {
        try {
            adminService.freezePost(userId, freezePostId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping(value = "/release/{releasePostId}")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<?> releasePost(@RequestAttribute Long userId,
                                 @PathVariable Long releasePostId) {
        try {
            adminService.releasePost(userId, releasePostId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
