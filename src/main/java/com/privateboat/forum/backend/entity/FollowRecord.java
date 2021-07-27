package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.enumerate.FollowStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table
public class FollowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long toUserId;

    @OneToOne
    @JoinColumn
    private UserInfo fromUser;

    private Timestamp timestamp;

    @Transient
    private FollowStatus followStatus;
}
