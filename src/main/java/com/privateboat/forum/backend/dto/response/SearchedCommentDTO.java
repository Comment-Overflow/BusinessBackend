package com.privateboat.forum.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.Objects;

@Getter
@Setter
public class SearchedCommentDTO {
    // Id of the post of the comment.
    Long id;
    String title;
    Integer commentCount;
    Comment hostComment;
    Comment searchedComment;
    Boolean isStarred;
    Boolean isFrozen;

    @JsonIgnore
    Post post;

    public SearchedCommentDTO(Post post, Comment searchedComment) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.commentCount = post.getCommentCount();
        this.hostComment = post.getHostComment();
        this.searchedComment = searchedComment;
        this.isFrozen = post.getIsFrozen();
        this.post = post;
        // isStarred should be set afterwards.
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == SearchedCommentDTO.class &&
                Objects.equals(((SearchedCommentDTO) obj).getId(), this.id);
    }
}
