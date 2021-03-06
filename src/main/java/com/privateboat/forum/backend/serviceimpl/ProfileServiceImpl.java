package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.request.ProfileSettingRequestDTO;
import com.privateboat.forum.backend.dto.response.ProfileDTO;
import com.privateboat.forum.backend.dto.response.ProfileSettingDTO;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.FollowStatus;
import com.privateboat.forum.backend.enumerate.Gender;
import com.privateboat.forum.backend.exception.ProfileException;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.FollowRecordRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.ProfileService;
import com.privateboat.forum.backend.util.ImageUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@AllArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {
    private final UserInfoRepository userInfoRepository;
    private final FollowRecordRepository followRecordRepository;
    private final ProjectionFactory projectionFactory;

    private final Environment environment;
    static private final String imageFolderName = "personalInfo/";

    @Override
    public UserInfo.UserNameAndAvatarUrl putProfile(Long userId, ProfileSettingRequestDTO profileSettingRequestDTO) throws ProfileException{
        UserInfo userInfo = userInfoRepository.getById(userId);
        if (profileSettingRequestDTO.getAvatar() != null) {
            String avatarFileName = String.format("%d_%s", userId, RandomStringUtils.randomAlphanumeric(6));
            if (!ImageUtil.uploadImage(profileSettingRequestDTO.getAvatar(), avatarFileName, imageFolderName)) {
                throw new ProfileException(ProfileException.ProfileExceptionType.UPLOAD_IMAGE_FAILED);
            }
            String imageUrl = environment.getProperty("com.privateboat.forum.backend.image-base-url") + imageFolderName + avatarFileName;
            userInfo.setAvatarUrl(imageUrl);
        }
        userInfo.setBrief(profileSettingRequestDTO.getBrief());
        switch (profileSettingRequestDTO.getGender()) {
            case "???":
                userInfo.setGender(Gender.MALE);
                break;
            case "???":
                userInfo.setGender(Gender.FEMALE);
                break;
            case "??????":
                userInfo.setGender(Gender.SECRET);
                break;
            default:
                throw new ProfileException(ProfileException.ProfileExceptionType.GENDER_NOT_VALID);
        }
        userInfo.setUserName(profileSettingRequestDTO.getUserName());
        userInfoRepository.save(userInfo);
        UserInfo.UserNameAndAvatarUrl userNameAndAvatarUrl = projectionFactory.createProjection(UserInfo.UserNameAndAvatarUrl.class, userInfo);
        return projectionFactory.createProjection(UserInfo.UserNameAndAvatarUrl.class, userInfo);
    }

    @Override
    public ProfileDTO getProfile(Long myUserId, Long otherUserId) {
        UserInfo userInfo = userInfoRepository.getById(otherUserId);
        FollowStatus followStatus = myUserId.equals(otherUserId) ? FollowStatus.NONE : followRecordRepository.getFollowStatus(myUserId, otherUserId);
        return new ProfileDTO(
                userInfo.getId(),
                userInfo.getUserName(),
                userInfo.getBrief(),
                userInfo.getAvatarUrl(),
                userInfo.getGender(),
                userInfo.getUserStatistic().getPostCount(),
                userInfo.getUserStatistic().getFollowerCount(),
                userInfo.getUserStatistic().getFollowingCount(),
                userInfo.getUserStatistic().getApprovalCount(),
                followStatus
        );
    }
}
