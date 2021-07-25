package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.enumerate.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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
