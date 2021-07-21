package com.privateboat.forum.backend.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table
public class FollowNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn
    private UserInfo followedUser;

    private Timestamp timestamp;

    @Transient
    private Boolean isMutual;
}
