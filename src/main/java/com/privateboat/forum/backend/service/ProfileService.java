package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.request.ProfileSettingDTO;
import com.privateboat.forum.backend.entity.UserInfo;

public interface ProfileService {
    void putProfile(Long userId, ProfileSettingDTO profileSettingDTO);
    UserInfo getProfile(Long userId);
}
