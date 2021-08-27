package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.enumerate.FollowStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(
        indexes = {
                @Index(columnList = "toUserId, from_user_id")
        }
)
public class FollowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long toUserId;

    @OneToOne
    @JoinColumn(nullable = false)
    private UserInfo fromUser;

    @Column(nullable = false)
    private Timestamp timestamp;

    @Transient
    private FollowStatus followStatus;
}
