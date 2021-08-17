package com.privateboat.forum.backend.rabbitmq;

import com.privateboat.forum.backend.configuration.RabbitMQConfig;
import com.privateboat.forum.backend.enumerate.MQMethod;
import com.privateboat.forum.backend.exception.RabbitMQException;
import com.privateboat.forum.backend.rabbitmq.bean.ApprovalBean;
import com.privateboat.forum.backend.rabbitmq.bean.FollowBean;
import com.privateboat.forum.backend.rabbitmq.bean.ReplyBean;
import com.privateboat.forum.backend.rabbitmq.bean.StarBean;
import com.privateboat.forum.backend.service.ApprovalRecordService;
import com.privateboat.forum.backend.service.FollowRecordService;
import com.privateboat.forum.backend.service.ReplyRecordService;
import com.privateboat.forum.backend.service.StarRecordService;
import com.privateboat.forum.backend.util.JacksonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MQReceiver {
    private final FollowRecordService followRecordService;
    private final ApprovalRecordService approvalRecordService;
    private final StarRecordService starRecordService;
    private final ReplyRecordService replyRecordService;

    @RabbitListener(queues = RabbitMQConfig.FOLLOW_QUEUE)
    public void followHandler(String msg) {
        log.info("Follow message received!");
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
        log.info("Approval message received!");
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
        log.info("Star message received!");
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
        log.info("Reply message received!");
        ReplyBean bean = JacksonUtil.json2Bean(msg, ReplyBean.class);
        checkNullBean(bean);
        assert bean != null;
        replyRecordService.postReplyRecord(bean.getUserId(), bean.getDto());
    }

    void checkNullBean(Object bean) throws RabbitMQException {
        if (bean == null) {
            throw new RabbitMQException(RabbitMQException.RabbitMQExceptionType.NULL_BEAN);
        }
    }
}
