package com.privateboat.forum.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserStatistic implements Serializable {
    @Id
    private Long userId;
    @Column(nullable = false)
    private Integer commentCount;
    @Column(nullable = false)
    private Integer postCount;
    @Column(nullable = false)
    private Integer followingCount;
    @Column(nullable = false)
    private Integer followerCount;
    @Column(nullable = false)
    private Integer approvalCount;
    @Column(nullable = false)
    private Boolean isNewlyApproved;
    @Column(nullable = false)
    private Boolean isNewlyReplied;
    @Column(nullable = false)
    private Boolean isNewlyStarred;
    @Column(nullable = false)
    private Boolean isNewlyFollowed;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo userInfo;

    public UserStatistic(UserInfo userInfo) {
        this.userInfo = userInfo;
        this.postCount = 0;
        this.commentCount = 0;
        this.postCount = 0;
        this.followingCount = 0;
        this.followerCount = 0;
        this.approvalCount = 0;
        this.isNewlyApproved = false;
        this.isNewlyFollowed = false;
        this.isNewlyReplied = false;
        this.isNewlyStarred = false;
    }

    public void addPost(){
        postCount++;
    }
    public void addFollowing(){
        followingCount++;
    }
    public void addFollower(){
        followerCount++;
    }
    public void addApproval(){
        approvalCount++;
    }
}
