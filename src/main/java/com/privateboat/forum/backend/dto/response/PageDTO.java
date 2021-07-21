package com.privateboat.forum.backend.dto.response;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageDTO<T> {
    List<T> content;
    Integer size;

    public PageDTO(Page<T> page) {
        content = page.getContent();
        size = page.getNumberOfElements();
    }
}
