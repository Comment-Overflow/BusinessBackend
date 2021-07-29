package com.privateboat.forum.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextMessageDTO {
    Long receiverId;
    String content;
}
