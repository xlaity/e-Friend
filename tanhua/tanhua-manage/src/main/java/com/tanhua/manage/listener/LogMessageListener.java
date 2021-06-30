package com.tanhua.manage.listener;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.tanhua.manage.domain.Log;
import com.tanhua.manage.service.LogService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@RocketMQMessageListener(topic = "tanhua-log", consumerGroup = "tanhua-log-consumer")
public class LogMessageListener implements RocketMQListener<String> {

    @Autowired
    private LogService logService;

    @Override
    public void onMessage(String message) {
        System.out.println("消息内容 = " + message);
        // 将json字符串转为map
        Map<String, Object> map = JSON.parseObject(message, Map.class);

        // userId 默认解析为Integer类型，不能直接转成Long，因此如下处理
        Long userId = Long.valueOf(map.get("userId").toString());
        String type = (String) map.get("type");
        String logTime = (String) map.get("logTime");

        // 保存日志到数据库
        Log log = new Log();
        log.setUserId(userId);
        log.setLogTime(logTime);
        log.setType(type);
        log.setCreated(new Date());
        log.setUpdated(new Date());

        logService.save(log);

    }
}


