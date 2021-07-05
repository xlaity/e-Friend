package com.tanhua.manage.controller;

import com.tanhua.manage.service.AnalysisByDayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class AnalysisController {

    @Autowired
    private AnalysisByDayService analysisByDayService;

    /**
     * 接口名称：概要统计信息
     * 接口路径：GET/dashboard/summary
     */
    @GetMapping("dashboard/summary")
    public ResponseEntity<Object> summary() {
        return analysisByDayService.summary();
    }

    /**
     * 接口名称：新增、活跃用户、次日留存率
     * 接口路径：GET/dashboard/users
     */
    @GetMapping("/dashboard/users")
    public ResponseEntity<Object> findAddUsers(@RequestHeader("Authorization")String token,
                                               @RequestParam("sd")Long sd,
                                               @RequestParam("ed")Long ed,
                                               @RequestParam("type")Integer type) {
        log.info("接口名称：新增、活跃用户、次日留存率");


        return analysisByDayService.findAddUser(sd,ed,type);
    }
}
