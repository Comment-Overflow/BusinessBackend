package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.enumerate.PostTag;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Data
@Table
@NoArgsConstructor
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "user_id")
    private Long userId;
    private String searchKey;
    private PostTag postTag;
    private Timestamp time;

    public SearchHistory(Long userId, String searchKey, PostTag postTag) {
        this.userId = userId;
        this.searchKey = searchKey;
        this.postTag = postTag;
        this.time = Timestamp.from(Instant.now());
    }
}
