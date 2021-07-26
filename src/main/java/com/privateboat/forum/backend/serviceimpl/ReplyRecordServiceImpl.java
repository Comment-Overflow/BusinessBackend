package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.request.ReplyRecordReceiveDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.ReplyRecord;
import com.privateboat.forum.backend.enumerate.RecordType;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.PostRepository;
import com.privateboat.forum.backend.repository.ReplyRecordRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.repository.UserStatisticRepository;
import com.privateboat.forum.backend.service.ReplyRecordService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;

@Service
@AllArgsConstructor
@Transactional
public class ReplyRecordServiceImpl implements ReplyRecordService {
    private final ReplyRecordRepository replyRecordRepository;
    private final UserInfoRepository userInfoRepository;
    private final PostRepository postRepository;
    private final UserStatisticRepository userStatisticRepository;

    @Override
    public Page<ReplyRecord> getReplyRecords(Long toUserId, Pageable pageable) {
        userStatisticRepository.setFlag(toUserId, RecordType.REPLY);
        return replyRecordRepository.getReplyRecords(toUserId, pageable);
    }

    @Override
    public void postReplyRecord(Long fromUserId, ReplyRecordReceiveDTO replyRecordReceiveDTO) throws UserInfoException, PostException {
        ReplyRecord replyRecord = new ReplyRecord();

        replyRecord.setToUserId(replyRecordReceiveDTO.getToUserId());

        replyRecord.setTimestamp(new Timestamp(System.currentTimeMillis()));

        replyRecord.setFromUser(userInfoRepository.getById(fromUserId));

        Post post = postRepository.getByPostId(replyRecordReceiveDTO.getPostId());
        replyRecord.setPost(post);

        Comment comment = post.getComments().get(replyRecordReceiveDTO.getFloor());
        replyRecord.setComment(comment);

        userStatisticRepository.setFlag(replyRecordReceiveDTO.getToUserId(), RecordType.REPLY);
        userStatisticRepository.getByUserId(replyRecordReceiveDTO.getToUserId()).addPost();

        replyRecordRepository.save(replyRecord);
    }
}
