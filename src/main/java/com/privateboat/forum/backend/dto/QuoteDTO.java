package com.privateboat.forum.backend.dto;

import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.UserInfo;
import lombok.Data;

@Data
public class QuoteDTO {
    private String postTitle;
    private Integer floor;
    private String content;
    private UserInfo userInfo;

    public QuoteDTO(Comment comment) {
        postTitle = comment.getPost().getTitle();
        floor = comment.getFloor();
        content = comment.getContent();
        userInfo = comment.getUserInfo();
    }
}
