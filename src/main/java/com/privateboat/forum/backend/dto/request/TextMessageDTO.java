package com.privateboat.forum.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextMessageDTO {

    String uuid;
    Long senderId;
    Long receiverId;
    String content;

}
