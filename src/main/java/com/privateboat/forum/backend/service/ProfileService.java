package com.privateboat.forum.backend.service;

import com.privateboat.forum.backend.dto.request.ProfileSettingRequestDTO;
import com.privateboat.forum.backend.dto.response.ProfileDTO;
import com.privateboat.forum.backend.entity.UserInfo;

public interface ProfileService {
    UserInfo.UserNameAndAvatarUrl putProfile(Long userId, ProfileSettingRequestDTO profileSettingRequestDTO);
    ProfileDTO getProfile(Long myUserId, Long otherUserId);
}
