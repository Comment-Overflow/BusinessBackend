package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.request.ProfileSettingRequestDTO;
import com.privateboat.forum.backend.dto.response.ProfileDTO;
import com.privateboat.forum.backend.exception.ProfileException;
import com.privateboat.forum.backend.service.ProfileService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping(value = "/profiles/{otherUserId}")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<ProfileDTO> getProfile(@RequestAttribute Long userId,
                                          @PathVariable Long otherUserId) {
        try {
            return ResponseEntity.ok(profileService.getProfile(userId, otherUserId));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping(value = "/profiles/settings")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<?> putProfile(@RequestAttribute Long userId,
                                                             ProfileSettingRequestDTO profileSettingRequestDTO) {
        try {
            return ResponseEntity.ok(profileService.putProfile(userId, profileSettingRequestDTO));
        } catch (ProfileException e) {
            String message = e.getMessage();
            if (e.getType() == ProfileException.ProfileExceptionType.UPLOAD_IMAGE_FAILED) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
}
