package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.AuthDTO;
import com.privateboat.forum.backend.exception.AuthException;
import com.privateboat.forum.backend.service.AuthService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/user")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<?> register(AuthDTO authDTO) {
        try {
            authService.register(authDTO.getEmail(), authDTO.getPassword());
            return ResponseEntity.ok().build();
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }

    // TODO: Change url name.
    @PostMapping(value = "/sessions")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<String> login(AuthDTO authDTO) {
        try {
            return ResponseEntity.ok(authService.login(authDTO.getEmail(), authDTO.getPassword()));
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
