package com.privateboat.forum.backend.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table
public class ReplyNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long from;

    private Long to;

    private Timestamp timestamp;

    private Long commentId;

    private Long quoteId;
}
