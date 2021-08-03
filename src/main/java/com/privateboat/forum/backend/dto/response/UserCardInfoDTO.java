package com.privateboat.forum.backend.dto.response;

import com.privateboat.forum.backend.enumerate.FollowStatus;
import com.privateboat.forum.backend.enumerate.UserType;
import lombok.Value;

@Value
public class UserCardInfoDTO {
    Long userId;
    String userName;
    String avatarUrl;

    String brief;
    Integer commentCount;
    Integer followerCount;
    FollowStatus followStatus;
    UserType userType;
}
