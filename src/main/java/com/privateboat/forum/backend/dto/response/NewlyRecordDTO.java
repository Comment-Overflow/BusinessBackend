package com.privateboat.forum.backend.dto.response;

import lombok.Data;

@Data
public class NewlyRecordDTO {
    private Boolean isNewlyApproved;
    private Boolean isNewlyReplied;
    private Boolean isNewlyStarred;
    private Boolean isNewlyFollowed;
}
