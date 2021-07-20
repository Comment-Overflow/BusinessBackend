package com.privateboat.forum.backend.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = UserInfo.class, fetch = FetchType.LAZY)
    private UserInfo userInfo;
    private String title;
    private Integer commentCount;

    @OneToMany(cascade = CascadeType.REMOVE,
            targetEntity = Comment.class,
            fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @Transient
    private Comment hostComment;
}
