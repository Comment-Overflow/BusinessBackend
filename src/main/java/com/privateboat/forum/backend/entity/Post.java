package com.privateboat.forum.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserInfo userInfo;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private Integer commentCount;
    @Column(nullable = false)
    private Timestamp postTime;
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private PostTag tag;

    @OneToMany(cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY,
            mappedBy = "post")
    @JsonIgnore
    private List<Comment> comments;

    @JsonIgnore
    @Column(nullable = false)
    private Boolean isDeleted;

    @OneToOne(fetch = FetchType.LAZY)
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
    }

    public void setTransientProperties() {

    }
}
