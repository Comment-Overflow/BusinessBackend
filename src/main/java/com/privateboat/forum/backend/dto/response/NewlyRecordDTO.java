package com.privateboat.forum.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewlyRecordDTO {
    private Boolean isNewlyApproved;
    private Boolean isNewlyReplied;
    private Boolean isNewlyStarred;
    private Boolean isNewlyFollowed;
}
