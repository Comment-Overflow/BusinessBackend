package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.enumerate.Gender;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    @Enumerated(EnumType.ORDINAL)
    private Gender gender;
    private String brief;
    private String avatarUrl;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "userInfo")
    UserAuth userAuth;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "userInfo")
    UserStatistic userStatistic;

    public interface IdAndUserNameAndAvatarUrl {
        Long getId();
        String getUserName();
        String getAvatarUrl();
    }
}