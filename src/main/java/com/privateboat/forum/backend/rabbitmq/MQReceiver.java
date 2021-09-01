package com.privateboat.forum.backend.rabbitmq;

import com.privateboat.forum.backend.configuration.RabbitMQConfig;
import com.privateboat.forum.backend.entity.Message;
import com.privateboat.forum.backend.enumerate.MQMethod;
import com.privateboat.forum.backend.exception.RabbitMQException;
import com.privateboat.forum.backend.rabbitmq.bean.*;
import com.privateboat.forum.backend.repository.CommentRepository;
import com.privateboat.forum.backend.repository.UserStatisticRepository;
import com.privateboat.forum.backend.service.*;
import com.privateboat.forum.backend.util.JacksonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class MQReceiver {
    private final FollowRecordService followRecordService;
    private final ApprovalRecordService approvalRecordService;
    private final StarRecordService starRecordService;
    private final ReplyRecordService replyRecordService;
    private final CommentRepository commentRepository;
    private final UserStatisticRepository userStatisticRepository;
    private final ChatService chatService;

    @RabbitListener(queues = RabbitMQConfig.FOLLOW_QUEUE)
    public void followHandler(String msg) {
        // log.info("Follow message received!");
        FollowBean bean = JacksonUtil.json2Bean(msg, FollowBean.class);
        checkNullBean(bean);
        assert bean != null;
        if (bean.getMethod() == MQMethod.POST) {
            followRecordService.postFollowRecord(bean.getFromUserId(), bean.getToUserId());
        } else { // Method.DELETE
            followRecordService.deleteFollowRecord(bean.getFromUserId(), bean.getToUserId());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.APPROVAL_QUEUE)
    public void approvalHandler(String msg) {
        // log.info("Approval message received!");
        ApprovalBean bean = JacksonUtil.json2Bean(msg, ApprovalBean.class);
        checkNullBean(bean);
        assert bean != null;
        if (bean.getMethod() == MQMethod.POST) {
            approvalRecordService.postApprovalRecord(bean.getUserId(), bean.getDto());
        } else {
            approvalRecordService.deleteApprovalRecord(bean.getUserId(), bean.getDto());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.STAR_QUEUE)
    public void starHandler(String msg) {
        // log.info("Star message received!");
        StarBean bean = JacksonUtil.json2Bean(msg, StarBean.class);
        checkNullBean(bean);
        assert bean != null;
        if (bean.getMethod() == MQMethod.POST) {
            starRecordService.postStarRecord(bean.getFromUserId(), bean.getToUserId(), bean.getPostId());
        } else {
            starRecordService.deleteStarRecord(bean.getFromUserId(), bean.getPostId());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.REPLY_QUEUE)
    public void ReplyHandler(String msg) {
        // log.info("Reply message received!");
        ReplyBean bean = JacksonUtil.json2Bean(msg, ReplyBean.class);
        checkNullBean(bean);
        assert bean != null;
        replyRecordService.postReplyRecord(bean.getUserId(), bean.getDto());
    }

    @RabbitListener(queues = RabbitMQConfig.COMMENT_CACHE_UPDATE_QUEUE)
    public void CacheUpdateHandler(String msg) {
        // log.info("Update post caching...");
        CommentCacheUpdateBean bean = JacksonUtil.json2Bean(msg, CommentCacheUpdateBean.class);
        checkNullBean(bean);
        assert bean != null;
        Pageable pageable = PageRequest.of(bean.getPageNum(), bean.getPageSize(),
                Sort.by(Sort.Direction.ASC, "floor"));
        commentRepository.updateCommentCache(bean.getPostId(), pageable);
    }

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.POST_QUEUE)
    public void PostStatisticHandler(String msg) {
        log.info("updating post statistics...");
        StatisticBean bean = JacksonUtil.json2Bean(msg, StatisticBean.class);
        checkNullBean(bean);
        assert bean != null;
        if (bean.getIncrement()) {
            userStatisticRepository.addPost(bean.getUserId());
        } else {
            userStatisticRepository.subPost(bean.getUserId());
        }
    }

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.COMMENT_QUEUE)
    public void CommentStatisticHandler(String msg) {
        log.info("updating comment statistics...");
        StatisticBean bean = JacksonUtil.json2Bean(msg, StatisticBean.class);
        checkNullBean(bean);
        assert bean != null;
        if (bean.getIncrement()) {
            userStatisticRepository.addCommentCount(bean.getUserId());
        } else {
            userStatisticRepository.subCommentCount(bean.getUserId());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE)
    public void ChatHandler(String msg) {
        Message message = JacksonUtil.json2Bean(msg, Message.class);
        checkNullBean(message);
        assert message != null;
        chatService.updateChatOnNewMessage(message);
    }

    void checkNullBean(Object bean) throws RabbitMQException {
        if (bean == null) {
            throw new RabbitMQException(RabbitMQException.RabbitMQExceptionType.NULL_BEAN);
        }
    }
}
