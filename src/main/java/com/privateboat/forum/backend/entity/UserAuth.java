package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.enumerate.UserType;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UserAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    @Enumerated(EnumType.ORDINAL)
    private UserType userType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_info_id")
    private UserInfo userInfo;
}
