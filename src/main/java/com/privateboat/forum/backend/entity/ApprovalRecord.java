package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table
public class ApprovalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    ApprovalStatus approvalStatus;

    @OneToOne
    @JoinColumn(nullable = false)
    private UserInfo fromUser;

    @Column(nullable = false)
    private Long toUserId;

    @Column(nullable = false)
    private Timestamp timestamp;

    @OneToOne
    @JoinColumn(nullable = false)
    private Comment comment;
    // use comment.postId to get post Title
}
