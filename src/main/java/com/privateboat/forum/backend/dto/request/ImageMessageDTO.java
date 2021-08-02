package com.privateboat.forum.backend.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageMessageDTO {
    private Long receiverId;
    private MultipartFile imageFile;
}
