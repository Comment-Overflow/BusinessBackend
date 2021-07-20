package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long postId;
    String content;
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
    Boolean isStared;
}
