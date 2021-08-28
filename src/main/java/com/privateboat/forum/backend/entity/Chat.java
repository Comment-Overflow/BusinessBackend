package com.privateboat.forum.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        indexes = {
                @Index(columnList = "user_id"),
                @Index(columnList = "user_id, chatter_id")
        }
)
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = UserInfo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo user;

    @OneToOne(targetEntity = UserInfo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "chatter_id")
    private UserInfo chatter;

    @OneToOne(targetEntity = Message.class, fetch = FetchType.LAZY)
    private Message lastMessage;

    @Column(nullable = false)
    private int unreadCount;

    public Chat(UserInfo user, UserInfo chatter, Message lastMessage, int unreadCount) {
        this.user = user;
        this.chatter = chatter;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }
}
