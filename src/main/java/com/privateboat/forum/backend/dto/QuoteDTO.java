package com.privateboat.forum.backend.dto;

import com.privateboat.forum.backend.entity.Comment;
import lombok.Data;

@Data
public class QuoteDTO {
    private Long commentId;
    private String title;
    private Integer floor;
    private String content;

    public QuoteDTO(Comment comment) {
        commentId = comment.getId();
        title = comment.getUserInfo().getUserName();
        floor = comment.getFloor();
        content = comment.getContent();
    }
}
