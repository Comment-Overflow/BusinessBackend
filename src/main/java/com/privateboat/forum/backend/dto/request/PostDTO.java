package com.privateboat.forum.backend.dto.request;

import com.privateboat.forum.backend.enumerate.PostTag;
import lombok.Data;

@Data
public class PostDTO {
    PostTag tag;
    Integer pageNum;
    Integer pageSize;
}
