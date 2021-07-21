package com.privateboat.forum.backend.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table
public class StarNotification {
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
    private Post post;
}
