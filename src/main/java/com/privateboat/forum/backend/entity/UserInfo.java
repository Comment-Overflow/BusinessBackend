package com.privateboat.forum.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.privateboat.forum.backend.enumerate.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"userStatistic", "userAuth", "hibernateLazyInitializer", "handler"})
public class UserInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String brief;
    private String avatarUrl;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "userInfo",
            cascade = CascadeType.PERSIST)
    UserAuth userAuth;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "userInfo",
            cascade = CascadeType.PERSIST)
    UserStatistic userStatistic;

    public UserInfo() {
        this.userName = "ykfg_" + RandomStringUtils.randomAlphanumeric(5);
        this.gender = Gender.SECRET;
    }

    public interface MinimalUserInfo {
        Long getId();
        String getUserName();
        String getAvatarUrl();
    }
}
