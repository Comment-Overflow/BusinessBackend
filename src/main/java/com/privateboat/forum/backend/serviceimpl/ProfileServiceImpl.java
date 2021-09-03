package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.request.ProfileSettingRequestDTO;
import com.privateboat.forum.backend.dto.response.ProfileDTO;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.FollowStatus;
import com.privateboat.forum.backend.enumerate.Gender;
import com.privateboat.forum.backend.exception.ProfileException;
import com.privateboat.forum.backend.repository.FollowRecordRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.ProfileService;
import com.privateboat.forum.backend.util.audit.TextAuditResult;
import com.privateboat.forum.backend.util.audit.TextAuditResultType;
import com.privateboat.forum.backend.util.audit.TextAuditUtil;
import com.privateboat.forum.backend.util.image.ImageAuditException;
import com.privateboat.forum.backend.util.image.ImageUploadException;
import com.privateboat.forum.backend.util.image.ImageUtil;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
    public UserInfo.UserNameAndAvatarUrl putProfile(Long userId, ProfileSettingRequestDTO profileSettingRequestDTO) throws ProfileException {
        UserInfo userInfo = userInfoRepository.getById(userId);

        if (profileSettingRequestDTO.getAvatar() != null) {
            String avatarFileName = ImageUtil.getNewImageName(profileSettingRequestDTO.getAvatar());
            try {
                ImageUtil.uploadImage(profileSettingRequestDTO.getAvatar(), avatarFileName, imageFolderName);
            } catch (ImageAuditException e) {
                if (e.getResult().isConfirmed()) {
                    throw new ProfileException(ProfileException.ProfileExceptionType.ILLEGAL_AVATAR);
                }
            } catch (ImageUploadException e) {
                throw new ProfileException(ProfileException.ProfileExceptionType.UPLOAD_IMAGE_FAILED);
            }
            String imageUrl = environment.getProperty("com.privateboat.forum.backend.image-base-url") + imageFolderName + avatarFileName;
            userInfo.setAvatarUrl(imageUrl);
        }
        // Audit user name and brief.
        String brief = profileSettingRequestDTO.getBrief();
        String userName = profileSettingRequestDTO.getUserName();
        auditContent(userName, true);
        auditContent(brief, false);

        userInfo.setUserName(profileSettingRequestDTO.getUserName());
        userInfo.setBrief(brief);

        switch (profileSettingRequestDTO.getGender()) {
            case "男":
                userInfo.setGender(Gender.MALE);
                break;
            case "女":
                userInfo.setGender(Gender.FEMALE);
                break;
            case "保密":
                userInfo.setGender(Gender.SECRET);
                break;
            default:
                throw new ProfileException(ProfileException.ProfileExceptionType.GENDER_NOT_VALID);
        }

        userInfoRepository.save(userInfo);
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
                userInfo.getUserAuth().getUserType(),
                followStatus
        );
    }

    private void auditContent(String text, Boolean isUserName) {
        TextAuditResult auditResult = TextAuditUtil.auditText(text);
        if (auditResult.getResultType() == TextAuditResultType.NOT_OK) {
            if (isUserName) {
                throw new ProfileException(ProfileException.ProfileExceptionType.ILLEGAL_USER_NAME);
            } else {
                throw new ProfileException(ProfileException.ProfileExceptionType.ILLEGAL_BRIEF);
            }
        }
    }
}
