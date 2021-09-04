package com.privateboat.forum.backend.rabbitmq;

import com.privateboat.forum.backend.configuration.RabbitMQConfig;
import com.privateboat.forum.backend.dto.request.ApprovalRecordReceiveDTO;
import com.privateboat.forum.backend.dto.request.ReplyRecordReceiveDTO;
import com.privateboat.forum.backend.entity.Message;
import com.privateboat.forum.backend.enumerate.MQMethod;
import com.privateboat.forum.backend.enumerate.StatisticType;
import com.privateboat.forum.backend.rabbitmq.bean.*;
import com.privateboat.forum.backend.util.JacksonUtil;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MQSender {
    private final AmqpTemplate amqpTemplate;

    public void sendFollowMessage(Long fromUserId, Long toUserId, MQMethod method) {
        FollowBean followBean = new FollowBean(fromUserId, toUserId, method);
        String msg = JacksonUtil.bean2Json(followBean);
        amqpTemplate.convertAndSend(RabbitMQConfig.FOLLOW_QUEUE, msg);
    }

    public void sendApprovalMessage(Long userId, ApprovalRecordReceiveDTO dto, MQMethod method) {
        ApprovalBean approvalBean = new ApprovalBean(userId, dto, method);
        String msg = JacksonUtil.bean2Json(approvalBean);
        amqpTemplate.convertAndSend(RabbitMQConfig.APPROVAL_QUEUE, msg);
    }

    public void sendStarMessage(Long fromUserId, Long toUserId, Long postId, MQMethod method) {
        StarBean starBean = new StarBean(fromUserId, toUserId, postId, method);
        String msg = JacksonUtil.bean2Json(starBean);
        amqpTemplate.convertAndSend(RabbitMQConfig.STAR_QUEUE, msg);
    }

    public void sendReplyMessage(Long userId, ReplyRecordReceiveDTO dto) {
        ReplyBean replyBean = new ReplyBean(userId ,dto);
        String msg = JacksonUtil.bean2Json(replyBean);
        amqpTemplate.convertAndSend(RabbitMQConfig.REPLY_QUEUE, msg);
    }

    public void sendCacheUpdateMessage(Long postId, Integer pageNum, Integer pageSize) {
        CommentCacheUpdateBean bean = new CommentCacheUpdateBean(postId, pageNum, pageSize);
        String msg = JacksonUtil.bean2Json(bean);
        amqpTemplate.convertAndSend(RabbitMQConfig.COMMENT_CACHE_UPDATE_QUEUE, msg);
    }

    public void sendUpdateStatisticMessage(Long userId, StatisticType type) {
        StatisticBean bean = new StatisticBean(userId, type);
        String msg = JacksonUtil.bean2Json(bean);
        amqpTemplate.convertAndSend(RabbitMQConfig.STATISTIC_QUEUE, msg);
    }

    public void updateChat(Message message) {
        String msg = JacksonUtil.bean2Json(message);
        amqpTemplate.convertAndSend(RabbitMQConfig.CHAT_QUEUE, msg);
    }

}
