package com.tanhua.recommend.listener;

import com.alibaba.fastjson.JSON;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.PublishScore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;


@Component
@RocketMQMessageListener(topic = "tanhua-quanzi", consumerGroup = "tanhua-quanzi-group")
@Slf4j
public class PublishScoreListener implements RocketMQListener<String> {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 1、转化map集合
     * 2、获取参数（userId,publishId,pid，type）
     * 3、构造对象 publishScore
     * 4、根据不同的type，设置不同的评分
     * 5、保存到mongodb中
     */
    public void onMessage(String message) {
        try {
            // 1、转化map集合
            Map<String, Object> msg = JSON.parseObject(message, Map.class);

            // 2、获取参数（userId,publishId,pid,type）
            Long userId = Long.valueOf(msg.get("userId").toString());
            String publishId = (String) msg.get("publishId");
            Long pid = Long.valueOf(msg.get("pid").toString());
            Integer type = Integer.valueOf(msg.get("type").toString());

            // 3、构造对象 publishScore
            PublishScore ps = new PublishScore();
            ps.setUserId(userId);
            ps.setPublishId(pid);
            ps.setDate(System.currentTimeMillis());

            // 4、根据不同的type，设置不同的评分
            // 1-发动态，2-浏览动态，3-点赞，4-喜欢，5-评论，6-取消点赞，7-取消喜欢
            switch (type) {
                case 1: {
                    int score = 0;
                    Publish publish = mongoTemplate.findById(new ObjectId(publishId), Publish.class);
                    if (StringUtils.length(publish.getTextContent()) < 50) {
                        score += 1;
                    } else if (StringUtils.length(publish.getTextContent()) < 100) {
                        score += 2;
                    } else if (StringUtils.length(publish.getTextContent()) >= 100) {
                        score += 3;
                    }
                    if (!CollectionUtils.isEmpty(publish.getMedias())) {
                        score += publish.getMedias().size();
                    }
                    ps.setScore(Double.valueOf(score));
                    break;
                }
                case 2: {
                    ps.setScore(1d);
                    break;
                }
                case 3: {
                    ps.setScore(5d);
                    break;
                }
                case 4: {
                    ps.setScore(8d);
                    break;
                }
                case 5: {
                    ps.setScore(10d);
                    break;
                }
                case 6: {
                    ps.setScore(-5d);
                    break;
                }
                case 7: {
                    ps.setScore(-8d);
                    break;
                }
                default: {
                    ps.setScore(0d);
                    break;
                }
            }
            // 5、保存到mongodb中
            mongoTemplate.save(ps);

        }catch (Exception e) {
            log.error("消费消息出错，错误信息：", e);
        }
    }
}
