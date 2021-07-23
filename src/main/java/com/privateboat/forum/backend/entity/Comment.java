package com.privateboat.forum.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privateboat.forum.backend.dto.QuoteDTO;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
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
    private Long quoteId;
    @Length(max = 300)
    private String content;
    private Timestamp time;
    private Integer floor;
    private Integer approvalCount;
    private Integer disapprovalCount;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "comment_image",
            joinColumns = {@JoinColumn(name = "comment_id" ,referencedColumnName = "id")},
            uniqueConstraints =  {@UniqueConstraint(columnNames={"comment_id", "image_url"})})
    @Column(name = "image_url")
    private List<String> imageUrl;

    @Transient
    private ApprovalStatus approvalStatus;
    @Transient
    private Boolean isStarred;
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
    }
}
