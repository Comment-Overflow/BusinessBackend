package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.recordreceive.ReplyRecordReceiveDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.ReplyRecord;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.CommentRepository;
import com.privateboat.forum.backend.repository.PostRepository;
import com.privateboat.forum.backend.repository.ReplyRecordRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.service.ReplyRecordService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;

@Transactional
@Service
@AllArgsConstructor
public class ReplyRecordServiceImpl implements ReplyRecordService {
    private final ReplyRecordRepository replyRecordRepository;
    private final UserInfoRepository userInfoRepository;
    private final PostRepository postRepository;

    @Override
    public Page<ReplyRecord> getReplyRecords(Long toUserId, Pageable pageable) {
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
    }
}
