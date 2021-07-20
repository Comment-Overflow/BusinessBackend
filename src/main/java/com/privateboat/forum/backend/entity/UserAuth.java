package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.enumerate.UserType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class UserAuth implements Serializable {
    @Id
    private Long userId;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo userInfo;

    public UserAuth(String email, String password, UserInfo userInfo) {
        this.email = email;
        this.password = password;
        this.userType = UserType.USER;
        this.userInfo = userInfo;
    }
}
