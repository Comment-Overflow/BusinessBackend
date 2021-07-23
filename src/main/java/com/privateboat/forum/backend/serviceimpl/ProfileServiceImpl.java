package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.request.ProfileSettingDTO;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {
    private final UserInfoRepository userInfoRepository;

    @Override
    public void putProfile(Long userId, ProfileSettingDTO profileSettingDTO) {
        UserInfo userInfo = userInfoRepository.getById(userId);

    }
}
