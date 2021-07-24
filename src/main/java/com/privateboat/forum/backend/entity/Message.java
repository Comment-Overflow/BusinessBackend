package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.enumerate.MessageType;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
public class Message {
    @Id
    private String id;

    @OneToOne(targetEntity = UserInfo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private UserInfo sender;

    @OneToOne(targetEntity = UserInfo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private UserInfo receiver;

    private Timestamp time;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private String content;
}
