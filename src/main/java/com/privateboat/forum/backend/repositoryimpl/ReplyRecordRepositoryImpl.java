package com.privateboat.forum.backend.repositoryimpl;

import com.privateboat.forum.backend.dao.ReplyRecordDAO;
import com.privateboat.forum.backend.entity.ReplyRecord;
import com.privateboat.forum.backend.repository.ReplyRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ReplyRecordRepositoryImpl implements ReplyRecordRepository {
    ReplyRecordDAO replyRecordDAO;

    @Override
    public Page<ReplyRecord> getReplyRecords(Long toUserId, Pageable pageable) {
        Page<ReplyRecord> replyRecords = replyRecordDAO.getByToUserIdOrderByTimestampDesc(toUserId, pageable);
        replyRecords.forEach((replyRecord) -> {
            System.out.println(replyRecord.getFromUser().toString());
            replyRecord.getPost().setTransientProperties();
        });
        return replyRecords;
    }

    @Override
    public void save(ReplyRecord replyRecord){
        replyRecordDAO.saveAndFlush(replyRecord);
    }

    @Override
    public void deleteReplyRecord(Long fromUserId, Long commentId) {
        replyRecordDAO.deleteByFromUserIdAndCommentId(fromUserId, commentId);
    }
}
