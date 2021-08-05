package com.privateboat.forum.backend.configuration;

import com.privateboat.forum.backend.util.RedisUtil;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BootConfig implements ApplicationRunner {
    final RedisUtil redisUtil;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("-----------------------有可奉告业务后端启动成功！-----------------------");
        System.out.println("-----------------------检查Redis数据库初始化-----------------------");
        redisUtil.initializeRecord();
        System.out.println("-----------------------Redis数据库初始化检查完成-----------------------");
    }
}
