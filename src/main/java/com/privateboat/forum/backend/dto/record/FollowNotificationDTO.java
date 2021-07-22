package com.privateboat.forum.backend.dto.record;

import java.sql.Timestamp;

public class FollowNotificationDTO {
    private String username;
    private String avatarUrl;
    private Timestamp timestamp;
    Boolean isMutual;
}
