package com.privateboat.forum.backend;

import com.privateboat.forum.backend.configuration.GeneralConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableCaching
@SpringBootApplication
public class BusinessBackendApplication {
    public static void main(String[] args) {
        processArg(args);
        SpringApplication.run(BusinessBackendApplication.class, args);
    }

    private static void processArg(String[] args) {
        for (String arg: args) {
            if (!arg.startsWith("--")) continue;

            String processedArg = arg.substring(2);
            if (processedArg.equals("disable-audition")) {
                GeneralConfig.enableAudition = false;
                break;
            }
        }
    }
}
