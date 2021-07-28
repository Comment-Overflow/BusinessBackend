package com.privateboat.forum.backend.dto.response;

import com.privateboat.forum.backend.enumerate.FollowStatus;
import com.privateboat.forum.backend.enumerate.Gender;
import lombok.Value;

@Value
public class ProfileDTO {
    Long userId;
    String userName;
    String brief;
    String avatarUrl;
    Gender gender;
    Integer userStatisticCommentCount;
    Integer userStatisticFollowerCount;
    Integer userStatisticFollowingCount;
    Integer userStatisticApprovalCount;
    FollowStatus followStatus;//it would be null if you get your own profile page
}
