package com.privateboat.forum.backend.entity;

import com.privateboat.forum.backend.enumerate.PreferenceDegree;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PreferencePostID.class)
public class PreferencePost {

    @Id
    @Column(nullable = false)
    private Long userId;

    @Id
    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Integer preferenceDegree;

    @Column(nullable = false)
    private Timestamp browseTime;
}
