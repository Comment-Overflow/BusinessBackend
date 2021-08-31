package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.enumerate.PostTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(
        indexes = {
                @Index(columnList = "userId")
        }
)
public class PreferredWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String word;

    @Column(nullable = false)
    private Long score;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private PostTag postTag;

    public PreferredWord(Long userId, String word, Long score, PostTag postTag){
        this.userId = userId;
        this.word = word;
        this.score = score;
        this.postTag = postTag;
    }

    public interface wordWithId {
        Long getId();
        String getWord();
        Long getScore();
    }
}
