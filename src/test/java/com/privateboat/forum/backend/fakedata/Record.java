package com.privateboat.forum.backend.fakedata;

import com.privateboat.forum.backend.dto.request.ApprovalRecordReceiveDTO;
import com.privateboat.forum.backend.dto.response.NewlyRecordDTO;
import com.privateboat.forum.backend.entity.UserStatistic;
import com.privateboat.forum.backend.enumerate.ApprovalStatus;

public class Record {
    public static final Long USER_ID = 1L;
    public static final ApprovalRecordReceiveDTO approvalRecordReceiveDTO = new ApprovalRecordReceiveDTO(1L, 2L, ApprovalStatus.APPROVAL);
    public static final UserStatistic userStatistic = new UserStatistic();
    public static final NewlyRecordDTO newlyRecordDTO = new NewlyRecordDTO(true, true, true, true);
}
