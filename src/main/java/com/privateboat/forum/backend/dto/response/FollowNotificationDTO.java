package com.privateboat.forum.backend.dto.response;

import lombok.Data;

@Data
public class FollowNotificationDTO {

    private Long fromUserUserId;
    private String fromUserUserName;
    private String fromUserAvatarUrl;
    private Long timestamp;
    private Boolean isMutual;
}
