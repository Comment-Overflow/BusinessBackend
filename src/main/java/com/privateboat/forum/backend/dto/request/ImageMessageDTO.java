package com.privateboat.forum.backend.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ImageMessageDTO {

    private Long receiverId;
    private MultipartFile imageFile;
}
