package com.privateboat.forum.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.privateboat.forum.backend.dto.QuoteDTO;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo userInfo;
    @Column(nullable = false)
    private Long quoteId;
    @Column(nullable = false)
    @Length(max = 300)
    private String content;
    @Column(nullable = false)
    private Timestamp time;
    @Column(nullable = false)
    private Integer floor;
    @Column(nullable = false)
    private Integer approvalCount;
    @Column(nullable = false)
    private Integer disapprovalCount;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "comment_image",
            joinColumns = {@JoinColumn(name = "comment_id" ,referencedColumnName = "id")},
            uniqueConstraints =  {@UniqueConstraint(columnNames={"comment_id", "image_url"})})
    @Column(name = "image_url")
    @Fetch(value = FetchMode.SELECT)
    private List<String> imageUrl;

    @JsonIgnore
    @Column(nullable = false)
    private Boolean isDeleted;

    @Transient
    private ApprovalStatus approvalStatus;
    @Transient
    private QuoteDTO quoteDTO;

    public Comment(Post post, UserInfo userInfo, Long quoteId, String content) {
        this.post = post;
        this.userInfo = userInfo;
        this.quoteId = quoteId;
        this.content = content;
        this.time = new Timestamp(System.currentTimeMillis());
        this.floor = post.getCommentCount();
        this.approvalCount = 0;
        this.disapprovalCount = 0;
        this.imageUrl = new ArrayList<>();
        this.isDeleted = false;
    }

    public void addApproval() {
        ++approvalCount;
    }

    public void subApproval() {
        --approvalCount;
    }

    public void addDisapproval() {
        ++disapprovalCount;
    }

    public void subDisapproval() {
        --disapprovalCount;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", floor=" + floor +
                '}';
    }
}
