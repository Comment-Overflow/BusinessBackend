package com.privateboat.forum.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.privateboat.forum.backend.enumerate.PostTag;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(
        indexes = {
                @Index(columnList = "isDeleted, user_info_id, postTime"),
                @Index(columnList = "postTime"),
                @Index(columnList = "host_comment_id")
        }
)
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
    @Column(nullable = false)
    private Timestamp lastCommentTime;
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private PostTag tag;
    @Column(nullable = false)
    private Integer approvalCount;

    @OneToMany(cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY,
            mappedBy = "post")
    @JsonIgnore
    private List<Comment> comments;

    @JsonIgnore
    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    private Boolean isFrozen;

    @OneToOne(fetch = FetchType.LAZY)
    private Comment hostComment;
    @Transient
    private Boolean isStarred;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Basic(fetch = FetchType.LAZY)
    @Formula("(comment_count + approval_count) * 100 / POWER((DATE_PART('hour', now() - post_time) + 2), 1.8)")
    Integer hotIndex;

    public Post(String title, PostTag tag) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.title = title;
        this.tag = tag;
        this.commentCount = 0;
        this.approvalCount = 0;
        this.isDeleted = false;
        this.isFrozen = false;
        this.postTime = now;
        this.lastCommentTime = now;
        this.comments = new ArrayList<>();
    }

    public Post() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.title = "";
        this.tag = PostTag.LIFE;
        this.commentCount = 0;
        this.approvalCount = 0;
        this.isDeleted = false;
        this.isFrozen = false;
        this.postTime = now;
        this.lastCommentTime = now;
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

    public void incrementApproval() {
        ++approvalCount;
    }

    public void decrementApproval() {
        --approvalCount;
    }

    public void deleteComment(Comment comment) {
        comments.remove(comment);
    }

    public void setTransientProperties() {

    }
}
