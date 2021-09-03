package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.UserStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserStatisticDAO extends JpaRepository<UserStatistic, Long> {
    UserStatistic.NewRecord getNewlyRecordByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set approval_count = approval_count + 1 where user_id = ?1", nativeQuery = true)
    void incrementApprovalCountByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set approval_count = approval_count - 1 where user_id = ?1", nativeQuery = true)
    void decrementApprovalCountByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set comment_count = comment_count + 1 where user_id = ?1", nativeQuery = true)
    void incrementCommentCountByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set comment_count = comment_count - 1 where user_id = ?1", nativeQuery = true)
    void decrementCommentCountByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set following_count = following_count + 1 where user_id = ?1", nativeQuery = true)
    void incrementFollowingCountByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set following_count = following_count - 1 where user_id = ?1", nativeQuery = true)
    void decrementFollowingCountByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set follower_count = follower_count + 1 where user_id = ?1", nativeQuery = true)
    void incrementFollowerCountByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set follower_count = follower_count - 1 where user_id = ?1", nativeQuery = true)
    void decrementFollowerCountByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set post_count = post_count + 1 where user_id = ?1", nativeQuery = true)
    void incrementPostCountByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set post_count = post_count - 1 where user_id = ?1", nativeQuery = true)
    void decrementPostCountByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set approval_count = (select count(*) from approval_record where to_user_id = ?1) where user_id = ?1", nativeQuery = true)
    void updateApprovalCountByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set comment_count = (select count(*) from comment where comment.user_id = ?1 and is_deleted = false) where user_id = ?1", nativeQuery = true)
    void updateCommentCountByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set post_count = (select count(*) from post where user_info_id = ?1 and is_deleted = false) where user_id = ?1", nativeQuery = true)
    void updatePostCountByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set follower_count = (select count(*) from follow_record where to_user_id = ?1) where user_id = ?1", nativeQuery = true)
    void updateFollowerCountByUserId(Long userId);

    @Modifying
    @Query(value = "update user_statistic set follower_count = (select count(*) from follow_record where from_user_id = ?1) where user_id = ?1", nativeQuery = true)
    void updateFollowingCountByUserId(Long userId);
}
