package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.dto.QuoteDTO;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo userInfo;
    private Long quoteId;
    @Length(max = 300)
    private String content;
    Timestamp time;
    Integer floor;
    Integer approvalCount;
    Integer disapprovalCount;

    @ElementCollection
    @CollectionTable(name = "comment_image",
            joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "image_url")
    List<String> imageUrl = new ArrayList<>();

    @Transient
    ApprovalStatus approvalStatus;
    @Transient
    Boolean isStarred;
    @Transient
    QuoteDTO quoteDTO;
}
