package com.privateboat.forum.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table
public class ReplyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    // 新回复的comment

    private Long quoteCommentId;
    // 0 means reply main post
    // otherwise use it to find comment

    @OneToOne
    @JoinColumn(nullable = false)
    private Post post;
    //comment being replied belongs to which post
}
