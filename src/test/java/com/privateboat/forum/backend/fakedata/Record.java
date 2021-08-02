package com.privateboat.forum.backend.fakedata;

import com.privateboat.forum.backend.dto.request.ApprovalRecordReceiveDTO;
import com.privateboat.forum.backend.dto.response.ApprovalRecordDTO;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.entity.UserStatistic;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public class Record {
    public static final Long VALID_USER_ID = 1L;
    public static final Long NOT_EXIST_USER_ID = 2L;
    public static final ApprovalRecordReceiveDTO VALID_APPROVAL = new ApprovalRecordReceiveDTO(1L, 2L, ApprovalStatus.APPROVAL);
    public static final ApprovalRecordReceiveDTO TO_USER_NOT_EXIST_APPROVAL = new ApprovalRecordReceiveDTO(1L, 3L, ApprovalStatus.APPROVAL);
    public static final ApprovalRecordReceiveDTO COMMENT_NOT_EXIST_APPROVAL = new ApprovalRecordReceiveDTO(2L, 2L, ApprovalStatus.APPROVAL);

    public static final UserStatistic.NewlyRecord userStatisticNewlyRecord = new UserStatistic.NewlyRecord() {
        @Override
        public Boolean getIsNewlyApproved() {
            return true;
        }

        @Override
        public Boolean getIsNewlyReplied() {
            return true;
        }

        @Override
        public Boolean getIsNewlyStarred() {
            return true;
        }

        @Override
        public Boolean getIsNewlyFollowed() {
            return true;
        }
    };
    public static int PAGE_OFFSET = 1;
    public static int PAGE_SIZE = 1;
    public static Long VALID_COMMENT_ID = 1L;
    public static Long NOT_EXIST_COMMENT_ID = 2L;
    public static Pageable PAGEABLE = PageRequest.of(PAGE_OFFSET, PAGE_SIZE);
}
