package com.privateboat.forum.backend.dto.response;

import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import lombok.Value;

@Value
public class SearchedCommentDTO {
    // Id of the post of the comment.
    Long id;
    String title;
    Integer commentCount;
    Comment hostComment;
    Comment searchedComment;
    Boolean isStarred;
    Boolean isFrozen;

    public SearchedCommentDTO(Post post, Comment searchedComment) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.commentCount = post.getCommentCount();
        this.hostComment = post.getHostComment();
        this.searchedComment = searchedComment;
        this.isStarred = post.getIsStarred();
        this.isFrozen = post.getIsFrozen();
    }
}
