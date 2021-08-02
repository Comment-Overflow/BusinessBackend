package com.privateboat.forum.backend.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class NewCommentDTO {
    private Long postId;
    private Long quoteId;
    private String content;
    private List<MultipartFile> uploadFiles = new ArrayList<>();
}
