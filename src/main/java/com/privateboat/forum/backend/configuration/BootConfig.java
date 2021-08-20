package com.privateboat.forum.backend.configuration;

import com.privateboat.forum.backend.util.RedisUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class BootConfig implements ApplicationRunner {
    final RedisUtil redisUtil;

    @Override
    public void run(ApplicationArguments args) {
        log.info("有可奉告业务后端启动成功！");
        log.info("检查Redis数据库初始化");
        redisUtil.initializeRecord();
        log.info("Redis数据库初始化检查完成");

        if (!GeneralConfig.enableAudition) {
            log.info("图片与文字审核已禁用");
        }
    }
}
