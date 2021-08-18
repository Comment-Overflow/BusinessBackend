package com.privateboat.forum.backend.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String RECORD_EXCHANGE = "record-exchange";
    public static final String CACHE_EXCHANGE = "cache-exchange";

    public static final String FOLLOW_QUEUE = "follow-queue";
    public static final String APPROVAL_QUEUE = "approval-queue";
    public static final String STAR_QUEUE = "star-queue";
    public static final String REPLY_QUEUE = "reply-queue";
    public static final String COMMENT_CACHE_UPDATE_QUEUE = "cache-queue";

    public static final String FOLLOW_KEY = "follow";
    public static final String APPROVAL_KEY = "approval";
    public static final String STAR_KEY = "star";
    public static final String REPLY_KEY = "reply";
    public static final String CACHE_KEY = "cache";

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
    public DirectExchange recordExchange() {
        return new DirectExchange(RECORD_EXCHANGE);
    }

    @Bean
    public DirectExchange cacheExchange() {
        return new DirectExchange(CACHE_EXCHANGE);
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
}
