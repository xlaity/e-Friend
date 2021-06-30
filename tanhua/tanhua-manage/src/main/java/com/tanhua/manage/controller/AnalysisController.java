package com.tanhua.manage.controller;

import com.tanhua.manage.service.AnalysisByDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysisController {

    @Autowired
    private AnalysisByDayService analysisByDayService;
    /**
     * 接口名称：概要统计信息
     * 接口路径：GET/dashboard/summary
     */
    @GetMapping("dashboard/summary")
    public ResponseEntity<Object> summary(){
        return analysisByDayService.summary();
    }
}
