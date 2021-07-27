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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public Message(UserInfo sender, UserInfo receiver, Timestamp time, MessageType type, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.type = type;
        this.content = content;
    }
}
