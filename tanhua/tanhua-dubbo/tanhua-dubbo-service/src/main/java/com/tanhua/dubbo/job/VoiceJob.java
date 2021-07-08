package com.tanhua.dubbo.job;

import com.tanhua.dubbo.api.UserSumApi;
import com.tanhua.dubbo.api.VoiceApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @program: tanhua-group9
 * @create: 2021-07-03 10:18
 **/

/**
 * 江杰
 * @Params:
 * @Return
 */
//这个是一个定时任务 用于修改Voice中的次数
@Component
@Slf4j
public class VoiceJob {
    @Reference
    private UserSumApi userSumApi;
    //每日凌晨调用这个api
    @Scheduled(cron = "0 0 0 * * ?")  // 每日凌晨0点进行刷新
    public void analysis() {
        log.info("执行统计开始：--------->");
        userSumApi.updateTimes();
        log.info("执行统计结束：--------->");
    }
}
