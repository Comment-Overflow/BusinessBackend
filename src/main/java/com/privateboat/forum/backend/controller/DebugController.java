package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.util.audit.AuditUtil;
import com.privateboat.forum.backend.util.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {
    @GetMapping(value = "/")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<?> testConnection() {
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/audition")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<String> auditText(@RequestParam String text) {
        return ResponseEntity.ok(AuditUtil.auditText(text).getResultType().toString());
    }
}
