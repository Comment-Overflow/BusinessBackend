package com.privateboat.forum.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatHistoryDTO {

    private Long userId;
    private Long chatterId;
    private Integer pageNum;
    private Integer pageSize;

}
