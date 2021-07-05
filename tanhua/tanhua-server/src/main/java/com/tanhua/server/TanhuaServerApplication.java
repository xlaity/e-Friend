package com.tanhua.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@EnableCaching // 开启spring的注解缓存支持
@EnableScheduling // 开启定时任务支持
public class TanhuaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TanhuaServerApplication.class, args);
    }
}
