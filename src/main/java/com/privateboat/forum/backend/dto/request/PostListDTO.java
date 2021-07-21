package com.privateboat.forum.backend.dto.request;

import com.privateboat.forum.backend.enumerate.PostTag;
import lombok.Data;

@Data
public class PostListDTO {
    PostTag tag;
    Integer pageNum;
    Integer pageSize;
}
