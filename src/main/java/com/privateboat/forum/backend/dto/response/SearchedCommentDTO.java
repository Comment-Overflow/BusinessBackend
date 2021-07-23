package com.privateboat.forum.backend.dto.response;

import com.privateboat.forum.backend.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SearchedCommentDTO {
    private final Long postId;
    private final String title;
    private final Comment comment;
}
