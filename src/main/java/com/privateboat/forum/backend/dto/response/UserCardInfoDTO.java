package com.privateboat.forum.backend.dto.response;

import com.privateboat.forum.backend.enumerate.FollowStatus;
import com.privateboat.forum.backend.enumerate.UserType;
import lombok.*;
import org.apache.tomcat.jni.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCardInfoDTO {
    Long userId;
    String userName;
    String avatarUrl;
    String brief;
    Integer commentCount;
    Integer followerCount;
    UserType userType;

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class MyUserCardInfoDTO extends UserCardInfoDTO {
        FollowStatus followStatus;

        public MyUserCardInfoDTO(Long id, String userName, String avatarUrl, String brief, Integer commentCount, Integer followerCount, UserType userType, FollowStatus followStatus) {
            super(id, userName, avatarUrl, brief, commentCount, followerCount, userType);
            this.followStatus = followStatus;
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class OthersUserCardInfoDTO extends UserCardInfoDTO {
        public OthersUserCardInfoDTO(Long userId, String userName, String avatarUrl, String brief, Integer commentCount, Integer followerCount, UserType userType) {
            super(userId, userName, avatarUrl, brief, commentCount, followerCount, userType);
        }
    }
}
