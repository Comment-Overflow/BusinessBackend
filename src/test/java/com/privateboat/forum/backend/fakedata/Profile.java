package com.privateboat.forum.backend.fakedata;

import com.privateboat.forum.backend.dto.request.ProfileSettingRequestDTO;
import com.privateboat.forum.backend.dto.response.ProfileDTO;
import com.privateboat.forum.backend.enumerate.FollowStatus;
import com.privateboat.forum.backend.enumerate.Gender;
import org.springframework.web.multipart.MultipartFile;

public class Profile {
    public static final Long VALID_USER_ID = 1L;
    public static final Long NOT_EXIST_OTHER_USER_ID = 2L;
    public static final ProfileSettingRequestDTO VALID_PROFILE_SETTING_REQUEST_DTO = new ProfileSettingRequestDTO("wxp", "hello world", null, "男");
    public static final ProfileSettingRequestDTO GENDER_NOT_VALID_PROFILE_SETTING_REQUEST_DTO = new ProfileSettingRequestDTO("wxp", "hello world", null, "transition");
    public static final ProfileDTO PROFILE_DTO = new ProfileDTO(VALID_USER_ID, "wxp", "hello world", null, Gender.MALE, 0, 0, 0, 0, FollowStatus.NONE);
}
