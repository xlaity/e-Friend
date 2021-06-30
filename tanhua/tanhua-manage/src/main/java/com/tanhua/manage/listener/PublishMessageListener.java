package com.tanhua.manage.listener;

import com.tanhua.commons.templates.HuaWeiUGCTemplate;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.dubbo.api.PublishApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RocketMQMessageListener(topic = "tanhua-publish",
        consumerGroup = "tanhua-publish-consumer")
@Slf4j
public class PublishMessageListener implements RocketMQListener<String> {

    @Reference
    private PublishApi publishApi;

    @Autowired
    private HuaWeiUGCTemplate huaWeiUGCTemplate;

    @Override
    public void onMessage(String publishId) {
        log.info("动态id为：" + publishId);
        Publish publish = publishApi.findById(publishId);
        
        if (publish != null) {
            Integer state = publish.getState();
            // 获取动态的文字内容
            String textContent = publish.getTextContent();
            // 如果审核文字通过
            if(huaWeiUGCTemplate.textContentCheck(textContent)){
                List<String> medias = publish.getMedias();
                // 审核图片内容
                boolean imageContentCheck = huaWeiUGCTemplate.imageContentCheck(medias.toArray(new String[]{}));
                if(imageContentCheck){
                    state = 1;
                }else{
                    state = 2;
                }
            }else{
                state = 2;
            }
            // 更新动态的状态
            publishApi.updateState(publishId, state);
        }

    }
}
