package com.tanhua.dubbo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.tanhua.dubbo.mapper")  // 扫描包，将包下的接口都生成bean放到容器中
@EnableScheduling // 开启定时任务支持
public class DubboServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboServerApplication.class, args);
    }
}
