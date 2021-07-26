package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.request.ProfileSettingDTO;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.Gender;
import com.privateboat.forum.backend.exception.ProfileException;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.ProfileService;
import com.privateboat.forum.backend.util.Constant;
import com.privateboat.forum.backend.util.ImageUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@AllArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {
    private final UserInfoRepository userInfoRepository;

    static private final String imageFolderName = "personalInfo/";

    @Override
    public void putProfile(Long userId, ProfileSettingDTO profileSettingDTO) throws ProfileException{
        UserInfo userInfo = userInfoRepository.getById(userId);
        if(profileSettingDTO.getAvatar() != null) {
            String avatarFileName = String.format("%d_%s", userId, RandomStringUtils.randomAlphanumeric(6));
            if (!ImageUtil.uploadImage(profileSettingDTO.getAvatar(), avatarFileName, imageFolderName)) {
                throw new ProfileException(ProfileException.ProfileExceptionType.UPLOAD_IMAGE_FAILED);
            }
            userInfo.setAvatarUrl(Constant.serverUrl + imageFolderName + avatarFileName);
        }
        userInfo.setBrief(profileSettingDTO.getBrief());
        switch (profileSettingDTO.getGender()){
            case "男":
                userInfo.setGender(Gender.MALE);
                break;
            case "女":
                userInfo.setGender(Gender.FEMALE);
                break;
            case "保密":
                userInfo.setGender(Gender.SECRET);
                break;
        }
        userInfo.setUserName(profileSettingDTO.getUserName());
        userInfoRepository.save(userInfo);
    }

    @Override
    public UserInfo getProfile(Long userId) throws ProfileException{
        Optional<UserInfo> userInfo = userInfoRepository.findByUserId(userId);
        if(userInfo.isPresent()){
            return userInfo.get();
        }
        else throw new ProfileException(ProfileException.ProfileExceptionType.USER_NOT_FOUND);
    }
}
