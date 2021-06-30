package com.tanhua.manage.job;

import com.tanhua.manage.domain.AnalysisByDay;
import com.tanhua.manage.service.AnalysisByDayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AnalysisJob {

    @Autowired
    private AnalysisByDayService analysisByDayService;

    //    @Scheduled(cron = "* 0/5 * * * ?")  // 每隔5分钟执行一次
    @Scheduled(cron = "0/5 * * * * ?")  // 每隔5秒执行一次
    public void analysis() {
        log.info("执行统计开始：--------->");
        analysisByDayService.anasysis();
        log.info("执行统计结束：--------->");
    }
}
