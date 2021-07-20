package com.privateboat.forum.backend.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = UserInfo.class, fetch = FetchType.LAZY)
    private UserInfo userInfo;

    @OneToOne(targetEntity = Message.class, fetch = FetchType.LAZY)
    private Message lastMessage;

    private int unreadCount;
}
