package com.privateboat.forum.backend.dto.request;

import com.privateboat.forum.backend.enumerate.SortPolicy;
import lombok.Getter;

@Getter
public class CommentListDTO {
    private Long postId;
    private SortPolicy policy;
    private Integer pageNum;
    private Integer pageSize;
}
