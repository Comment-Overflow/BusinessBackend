package com.privateboat.forum.backend.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileSettingRequestDTO {
    private String userName;
    private String brief;
    private MultipartFile avatar;
    private String gender;
}
