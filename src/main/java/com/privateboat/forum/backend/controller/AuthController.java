package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.request.LoginDTO;
import com.privateboat.forum.backend.dto.request.RegisterDTO;
import com.privateboat.forum.backend.exception.AuthException;
import com.privateboat.forum.backend.service.AuthService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/users")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) {
        try {
            authService.register(registerDTO.getEmail(), registerDTO.getPassword(), registerDTO.getEmailToken());
            return ResponseEntity.ok().build();
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

    }

    @PostMapping(value = "/sessions")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            return ResponseEntity.ok(authService.login(loginDTO.getEmail(), loginDTO.getPassword()));
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping(value = "/sessions")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.BOTH)
    ResponseEntity<?> autoLogin(@RequestAttribute Long userId) {
        // If request reaches here, token verification must have been successful. No need to validate again.
        return ResponseEntity.ok().body(authService.refreshToken(userId));
    }

    @PostMapping(value = "/emails")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<?> sendEmail(String email) {
        authService.sendEmail(email);
        return ResponseEntity.ok().build();
    }
}
