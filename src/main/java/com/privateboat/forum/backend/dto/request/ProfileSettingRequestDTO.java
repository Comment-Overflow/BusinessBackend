package com.privateboat.forum.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileSettingRequestDTO {
    private String userName;
    private String brief;
    private MultipartFile avatar;
    private String gender;
}
