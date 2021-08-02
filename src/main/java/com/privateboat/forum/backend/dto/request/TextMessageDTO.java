package com.privateboat.forum.backend.dto.request;

import lombok.Data;

@Data
public class TextMessageDTO {
    Long receiverId;
    String content;
}
