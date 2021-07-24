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

    @Column
    @Enumerated(EnumType.STRING)
    ApprovalStatus approvalStatus;

    @OneToOne
    @JoinColumn
    private UserInfo fromUser;

    private Long toUserId;

    private Timestamp timestamp;

    @OneToOne
    @JoinColumn
    private Comment comment;
    // use comment.postId to get post Title
}
