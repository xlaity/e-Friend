package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.domain.mongo.Video;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 对动态的操作，发送mq消息到中间件
 */
@Service
@Slf4j
public class VideoMqService {

    @Reference
    private VideoApi videoApi;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发布视频
     */
    public void videoMsg(String videoId) {
        this.sendMsg(videoId, 1);
    }


    /**
     * 对视频点赞
     */
    public void likeVideoMsg(String videoId) {
        this.sendMsg(videoId, 2);
    }

    /**
     * 取消视频点赞
     */
    public void disLikeVideoMsg(String videoId) {
        this.sendMsg(videoId, 3);
    }

    /**
     * 评论视频
     */
    public void commentVideoMsg(String videoId) {
        this.sendMsg(videoId, 4);
    }


    /**
     * 发送消息
     * 参数：动态id
     * 参数type：1-发动态，2-点赞， 3-取消点赞，4-评论
     */
    private void sendMsg(String videoId, Integer type) {
        
        try {
            Video video = videoApi.findById(videoId);
            
            Map<String, Object> message = new HashMap<>();
            message.put("userId", UserHolder.getUserId());
            message.put("videoId", videoId);
            message.put("type", type);
            message.put("vid", video.getVid());
            
            rocketMQTemplate.convertAndSend("tanhua-video", JSON.toJSONString(message));
            
        }catch (Exception e) {
            log.error("发送失败，错误信息为：", e);
            log.error("发送消息失败! videoId = " + videoId + ", type = " + type);
        }
    }
}