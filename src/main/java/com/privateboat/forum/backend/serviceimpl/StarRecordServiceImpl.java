package com.privateboat.forum.backend.serviceimpl;

import com.privateboat.forum.backend.entity.Post;
import com.privateboat.forum.backend.entity.StarRecord;
import com.privateboat.forum.backend.entity.UserInfo;
import com.privateboat.forum.backend.enumerate.RecordType;
import com.privateboat.forum.backend.exception.PostException;
import com.privateboat.forum.backend.exception.UserInfoException;
import com.privateboat.forum.backend.repository.PostRepository;
import com.privateboat.forum.backend.repository.StarRecordRepository;
import com.privateboat.forum.backend.repository.UserInfoRepository;
import com.privateboat.forum.backend.repository.UserStatisticRepository;
import com.privateboat.forum.backend.service.StarRecordService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;

@Service
@AllArgsConstructor
@Transactional
public class StarRecordServiceImpl implements StarRecordService {
    private final UserInfoRepository userInfoRepository;
    private final StarRecordRepository starRecordRepository;
    private final PostRepository postRepository;
    private final UserStatisticRepository userStatisticRepository;

    @Override
    public Page<StarRecord> getStarRecords(Long userId, Pageable pageable){
        userStatisticRepository.removeFlag(userId, RecordType.STAR);
        return starRecordRepository.getStarRecords(userId, pageable);
    }

    @Override
    public void postStarRecord(Long fromUserId, Long toUserId, Long postId) throws UserInfoException, PostException {
        StarRecord newStarRecord = new StarRecord();

        newStarRecord.setFromUser(userInfoRepository.getById(fromUserId));

        newStarRecord.setToUserId(toUserId);

        newStarRecord.setTimestamp(new Timestamp(System.currentTimeMillis()));

        newStarRecord.setPost(postRepository.getByPostId(postId));

        if(!fromUserId.equals(toUserId)) {
            userStatisticRepository.setFlag(toUserId, RecordType.STAR);
        }

        starRecordRepository.save(newStarRecord);
    }

    @Override
    public void deleteStarRecord(Long fromUserId, Long postId) {
        starRecordRepository.deleteStarRecord(fromUserId, postId);
    }

    @Override
    public Boolean checkIfHaveStarred(Long userId, Long postId) throws PostException, UserInfoException {
        UserInfo userInfo = userInfoRepository.getById(userId);
        Post post = postRepository.getByPostId(postId);
        return starRecordRepository.checkIfHaveStarred(userInfo, post);
    }
}
