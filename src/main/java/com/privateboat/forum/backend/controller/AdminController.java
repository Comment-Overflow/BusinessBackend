package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AdminController {
    final AdminService adminService;
}
