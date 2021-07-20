package com.privateboat.forum.backend.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UserStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer commentCount;
    private Integer followingCount;
    private Integer followerCount;
    private Integer approvalCount;
    private Boolean isNewlyApproved;
    private Boolean isNewlyReplied;
    private Boolean isNewlyStarred;
    private Boolean isNewlyFollowed;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_info_id")
    private UserInfo userInfo;
}
