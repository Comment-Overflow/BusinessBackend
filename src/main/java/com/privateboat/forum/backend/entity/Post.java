package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.enumerate.PostTag;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserInfo userInfo;
    private String title;
    private Integer commentCount;
    private Timestamp postTime;
    @Enumerated(EnumType.STRING)
    private PostTag tag;

    @OneToMany(cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY,
            mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @Transient
    private Comment hostComment;

    public void setTransientProperties() {
        hostComment = comments.get(0);
    }
}
