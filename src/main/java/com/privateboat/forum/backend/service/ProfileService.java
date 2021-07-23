package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.request.ProfileSettingDTO;

public interface ProfileService {
    void putProfile(Long userId, ProfileSettingDTO profileSettingDTO);
}
