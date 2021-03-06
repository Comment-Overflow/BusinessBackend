package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.dto.request.ReplyRecordReceiveDTO;
import com.privateboat.forum.backend.dto.response.ReplyRecordDTO;
import com.privateboat.forum.backend.entity.Comment;
import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.ReplyRecord;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.RecordType;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.*;
import com.privateboat.forum.backend.service.ReplyRecordService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ReplyRecordServiceImpl implements ReplyRecordService {
    private final ReplyRecordRepository replyRecordRepository;
    private final UserInfoRepository userInfoRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserStatisticRepository userStatisticRepository;

    @Override
    public List<ReplyRecordDTO> getReplyRecords(Long toUserId, Pageable pageable) {
        userStatisticRepository.removeFlag(toUserId, RecordType.REPLY);
        return replyRecordRepository.getReplyRecords(toUserId, pageable).getContent().stream().map(replyRecord -> {
            UserInfo userInfo = replyRecord.getFromUser();
            Long quoteId = replyRecord.getQuoteCommentId();
            Comment quoteComment = quoteId == 0 ? replyRecord.getPost().getHostComment():commentRepository.getById(quoteId);
            Comment replyComment = replyRecord.getComment();
            return new ReplyRecordDTO(
                    userInfo.getId(),
                    userInfo.getUserName(),
                    userInfo.getAvatarUrl(),
                    replyRecord.getTimestamp().getTime(),
                    replyRecord.getPost().getTitle(),
                    quoteComment.getId(),
                    quoteComment.getContent(),
                    replyComment.getContent(),
                    replyComment.getId(),
                    replyComment.getFloor(),
                    quoteId == 0 ? 0 : quoteComment.getFloor()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public void postReplyRecord(Long fromUserId, ReplyRecordReceiveDTO replyRecordReceiveDTO) throws UserInfoException, PostException {
        ReplyRecord replyRecord = new ReplyRecord();

        replyRecord.setToUserId(replyRecordReceiveDTO.getToUserId());

        replyRecord.setTimestamp(new Timestamp(System.currentTimeMillis()));

        replyRecord.setFromUser(userInfoRepository.getById(fromUserId));

        Post post = postRepository.getByPostId(replyRecordReceiveDTO.getPostId());
        replyRecord.setPost(post);

        Comment comment = commentRepository.getById(replyRecordReceiveDTO.getCommentId());
        replyRecord.setComment(comment);

        System.out.println("quote Id is " + replyRecordReceiveDTO.getQuoteCommentId().toString());
        replyRecord.setQuoteCommentId(replyRecordReceiveDTO.getQuoteCommentId());

        userStatisticRepository.setFlag(replyRecordReceiveDTO.getToUserId(), RecordType.REPLY);

        replyRecordRepository.save(replyRecord);
    }
}
