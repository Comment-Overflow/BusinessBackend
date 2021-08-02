package com.privateboat.forum.backend.dto.response;

import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import lombok.Getter;

import java.util.List;

@Getter
public class PostContentDTO {
    private final Post post;
    private final List<Comment> comments;

    public PostContentDTO(Post post) {
        this.post = post;
        this.comments = post.getComments();
    }
}
