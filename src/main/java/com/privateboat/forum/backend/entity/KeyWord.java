package com.privateboat.forum.backend.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(
        indexes = {
                @Index(columnList = "postId")
        }
)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        KeyWord keyWord = (KeyWord) o;

        return Objects.equals(id, keyWord.id);
    }
}
