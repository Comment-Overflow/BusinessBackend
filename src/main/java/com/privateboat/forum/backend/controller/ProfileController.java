package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.request.ProfileSettingRequestDTO;
import com.privateboat.forum.backend.dto.response.ProfileDTO;
import com.privateboat.forum.backend.dto.response.ProfileSettingDTO;
import com.privateboat.forum.backend.exception.ProfileException;
import com.privateboat.forum.backend.service.ProfileService;
import com.privateboat.forum.backend.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
public class ProfileController {
    private final ModelMapper modelMapper;

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
    ResponseEntity<String> putProfile(@RequestAttribute Long userId,
                                      ProfileSettingRequestDTO profileSettingRequestDTO) {
        try{
            profileService.putProfile(userId, profileSettingRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (ProfileException e) {
            System.out.println(userId.toString() + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/profiles/settings")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<ProfileSettingDTO> getProfileSetting(@RequestAttribute Long userId){
        try{
            return ResponseEntity.ok(modelMapper.map(profileService.getProfileSetting(userId), ProfileSettingDTO.class));
        } catch (ProfileException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
