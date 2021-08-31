package com.privateboat.forum.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class KeyWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private String word;

    @Column(nullable = false)
    private Long score;

    public KeyWord(Long postId, String word, Long score) {
        this.postId = postId;
        this.word = word;
        this.score = score;
    }
}
