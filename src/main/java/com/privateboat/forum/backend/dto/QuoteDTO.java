package com.privateboat.forum.backend.dto;

import com.privateboat.forum.backend.entity.Comment;
import lombok.Data;

@Data
public class QuoteDTO {
    private String title;
    private Integer floor;
    private String content;

    public QuoteDTO(Comment comment) {
        title = comment.getPost().getTitle();
        floor = comment.getFloor();
        content = comment.getContent();
    }
}
