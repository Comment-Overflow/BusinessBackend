package com.privateboat.forum.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privateboat.forum.backend.dto.QuoteDTO;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import com.privateboat.forum.backend.util.FTSUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

    @ElementCollection
    @CollectionTable(name = "comment_image",
            joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "image_url")
    private List<String> imageUrl;

    @JsonIgnore
    private String contentForTSVector;
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
        this.contentForTSVector = StringUtils.join(FTSUtil.segment(content), " ");
    }
}
