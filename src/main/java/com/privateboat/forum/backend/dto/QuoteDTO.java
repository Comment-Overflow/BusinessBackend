package com.privateboat.forum.backend.dto;

import com.privateboat.forum.backend.entity.Comment;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuoteDTO {
    private Long commentId;
    private String title;
    private Integer floor;
    private String content;

    public QuoteDTO(Comment comment) {
        commentId = comment.getIsDeleted() ? -1 : comment.getId();
        title = comment.getUserInfo().getUserName();
        floor = comment.getFloor();
        content = comment.getContent();
    }
}
