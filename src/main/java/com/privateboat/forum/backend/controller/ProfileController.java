package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.dto.request.ProfileSettingDTO;
import com.privateboat.forum.backend.dto.response.ProfileDTO;
import com.privateboat.forum.backend.entity.UserInfo;
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

    @PutMapping(value = "/profiles")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<String> putProfile(@RequestAttribute Long userId,
                                      ProfileSettingDTO profileSettingDTO) {
        try{
            System.out.println(profileSettingDTO.toString());
            profileService.putProfile(userId, profileSettingDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (ProfileException e) {
            System.out.println(userId.toString() + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/profiles")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.USER)
    ResponseEntity<ProfileDTO> getProfile(@RequestAttribute Long userId){
        try{
            UserInfo userInfo = profileService.getProfile(userId);
            return ResponseEntity.ok(modelMapper.map(userInfo, ProfileDTO.class));
        } catch (ProfileException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
