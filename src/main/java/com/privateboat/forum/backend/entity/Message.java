package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.enumerate.MessageType;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = UserInfo.class, fetch = FetchType.LAZY)
    private UserInfo sender;

    @OneToOne(targetEntity = UserInfo.class, fetch = FetchType.LAZY)
    private UserInfo receiver;

    private Timestamp time;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private String content;
}
