package com.privateboat.forum.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table
public class ApprovalNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
