package com.privateboat.forum.backend.entity;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class PreferencePostID implements Serializable {
    private Long userId;
    private Long postId;

    public PreferencePostID(Long userId, Long postId){
        this.userId = userId;
        this.postId = postId;
    }

}
