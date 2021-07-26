package com.privateboat.forum.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privateboat.forum.backend.enumerate.PostTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserInfo userInfo;
    private String title;
    private Integer commentCount;
    private Timestamp postTime;
    @Enumerated(EnumType.STRING)
    private PostTag tag;

    @OneToMany(cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY,
            mappedBy = "post")
    @JsonIgnore
    private List<Comment> comments;

    @JsonIgnore
    private Boolean isDeleted;

    @Transient
    private Comment hostComment;
    @Transient
    private Boolean isStarred;

    public Post(String title, PostTag tag) {
        this.title = title;
        this.tag = tag;
        this.commentCount = 0;
        this.isDeleted = false;
        this.postTime = new Timestamp(System.currentTimeMillis());
        this.comments = new ArrayList<>();
    }

    public void setHostComment(Comment hostComment) {
        this.hostComment = hostComment;
        this.userInfo = hostComment.getUserInfo();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        commentCount++;
    }

    public void deleteComment(Comment comment) {
        comments.remove(comment);
        commentCount--;
    }

    public Comment getHostComment() {
        return comments.get(0);
    }

    public void setTransientProperties() {
        this.hostComment = comments.get(0);
    }
}
