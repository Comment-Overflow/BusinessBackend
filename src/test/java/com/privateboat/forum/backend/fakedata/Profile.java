package com.privateboat.forum.backend.fakedata;

import com.privateboat.forum.backend.dto.request.ProfileSettingRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public class Profile {
    public static final Long USER_ID = 1L;
    public static final ProfileSettingRequestDTO profileSettingRequestDTO = new ProfileSettingRequestDTO("wxp", "hello world", null, "男");
}
