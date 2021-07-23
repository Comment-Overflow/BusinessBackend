package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.request.ProfileSettingDTO;
import com.privateboat.forum.backend.dto.response.ApprovalRecordDTO;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class ProfileController {

    @PutMapping(value = "/profiles")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<List<ApprovalRecordDTO>> getApprovalRecords(@RequestAttribute Long userId,
                                                               ProfileSettingDTO profileSettingDTO) {

    }
}
