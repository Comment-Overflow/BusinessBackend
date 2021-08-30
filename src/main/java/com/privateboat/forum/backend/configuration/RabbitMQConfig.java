package com.privateboat.forum.backend.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String RECORD_EXCHANGE = "record-exchange";
    public static final String CACHE_EXCHANGE = "cache-exchange";
    public static final String STATISTIC_EXCHANGE = "statistic";
    public static final String CHAT_EXCHANGE = "chat-exchange";

    public static final String FOLLOW_QUEUE = "follow-queue";
    public static final String APPROVAL_QUEUE = "approval-queue";
    public static final String STAR_QUEUE = "star-queue";
    public static final String REPLY_QUEUE = "reply-queue";
    public static final String COMMENT_CACHE_UPDATE_QUEUE = "cache-queue";
    public static final String POST_QUEUE = "post-queue";
    public static final String COMMENT_QUEUE = "comment-queue";
    public static final String CHAT_QUEUE = "chat-queue";

    public static final String FOLLOW_KEY = "follow";
    public static final String APPROVAL_KEY = "approval";
    public static final String STAR_KEY = "star";
    public static final String REPLY_KEY = "reply";
    public static final String CACHE_KEY = "cache";
    public static final String POST_KEY = "post";
    public static final String COMMENT_KEY = "comment";
    public static final String CHAT_KEY = "chat";

    @Bean
    public Queue followQueue() {
        return new Queue(FOLLOW_QUEUE, true);
    }

    @Bean
    public Queue approvalQueue() {
        return new Queue(APPROVAL_QUEUE, true);
    }

    @Bean
    public Queue starQueue() {
        return new Queue(STAR_QUEUE, true);
    }

    @Bean
    public Queue replyQueue() {
        return new Queue(REPLY_QUEUE, true);
    }

    @Bean
    public Queue cacheQueue() {
        return new Queue(COMMENT_CACHE_UPDATE_QUEUE, true);
    }

    @Bean
    public Queue postQueue() {
        return new Queue(POST_QUEUE, true);
    }

    @Bean
    public Queue commentQueue() {
        return new Queue(COMMENT_QUEUE, true);
    }

    @Bean Queue chatQueue() {
        return new Queue(CHAT_QUEUE, true);
    }

    @Bean
    public DirectExchange recordExchange() {
        return new DirectExchange(RECORD_EXCHANGE);
    }

    @Bean
    public DirectExchange cacheExchange() {
        return new DirectExchange(CACHE_EXCHANGE);
    }

    @Bean
    public DirectExchange statisticExchange() {
        return new DirectExchange(STATISTIC_EXCHANGE);
    }

    @Bean
    public DirectExchange chatExchange() {
        return new DirectExchange(CHAT_EXCHANGE);
    }

    @Bean
    public Binding followBinding() {
        return BindingBuilder.bind(followQueue()).to(recordExchange()).with(FOLLOW_KEY);
    }

    @Bean
    public Binding approvalBinding() {
        return BindingBuilder.bind(approvalQueue()).to(recordExchange()).with(APPROVAL_KEY);
    }

    @Bean
    public Binding starBinding() {
        return BindingBuilder.bind(starQueue()).to(recordExchange()).with(STAR_KEY);
    }

    @Bean
    public Binding replyBinding() {
        return BindingBuilder.bind(replyQueue()).to(recordExchange()).with(REPLY_KEY);
    }

    @Bean
    public Binding cacheBinding() {
        return BindingBuilder.bind(cacheQueue()).to(cacheExchange()).with(CACHE_KEY);
    }

    @Bean
    public Binding postBinding() {
        return BindingBuilder.bind(postQueue()).to(statisticExchange()).with(POST_KEY);
    }

    @Bean
    public Binding commentBinding() {
        return BindingBuilder.bind(commentQueue()).to(statisticExchange()).with(COMMENT_KEY);
    }

    @Bean
    public Binding chatBinding() {
        return BindingBuilder.bind(chatQueue()).to(chatExchange()).with(CHAT_KEY);
    }
}
