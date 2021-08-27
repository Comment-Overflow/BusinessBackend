package com.privateboat.forum.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(
        indexes = {@Index(columnList = "from_user_id, post_id")}
)
public class StarRecord {
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
    private Post post;
}
