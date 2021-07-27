package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.ApprovalRecord;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApprovalRecordDAO extends JpaRepository<ApprovalRecord, Long> {
    Page<ApprovalRecord> findByToUserIdAndApprovalStatus(Long userId, ApprovalStatus approvalStatus, Pageable pageable);
    void deleteByToUserIdAndCommentId(Long userId, Long commentId);
    Optional<ApprovalRecord> findByFromUserAndComment(UserInfo userInfo, Comment comment);
}
