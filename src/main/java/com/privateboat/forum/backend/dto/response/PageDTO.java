package com.privateboat.forum.backend.dto.response;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PageDTO<T> {
    List<T> content;
    Long size;

    public PageDTO(Page<T> page) {
        content = page.getContent();
        size = page.getTotalElements();
    }

    public PageDTO(List<T> content, Long size) {
        this.content = content;
        this.size = size;
    }
}
