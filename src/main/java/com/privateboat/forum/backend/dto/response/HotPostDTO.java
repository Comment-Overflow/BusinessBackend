package com.privateboat.forum.backend.dto.response;

import com.privateboat.forum.backend.entity.Post;
import lombok.Data;

@Data
public class HotPostDTO {
    Post post;
    Integer hotIndex;

    public HotPostDTO(Post post) {
        this.post = post;
        this.hotIndex = post.getHotIndex();
    }
}
