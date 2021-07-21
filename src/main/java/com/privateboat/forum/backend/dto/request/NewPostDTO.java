package com.privateboat.forum.backend.dto.request;

import com.privateboat.forum.backend.enumerate.PostTag;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class NewPostDTO {
    private Long userId;
    private String title;
    private PostTag tag;
    private String content;
    private List<MultipartFile> uploadFiles = new ArrayList<>();
}
