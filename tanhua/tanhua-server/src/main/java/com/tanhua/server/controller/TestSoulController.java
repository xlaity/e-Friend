package com.tanhua.server.controller;

import com.tanhua.domain.entity.Answer;
import com.tanhua.server.service.TestSoulService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
@RequestMapping("/testSoul")
public class TestSoulController {

    @Autowired
    private TestSoulService testSoulService;

    /**
     * 接口名称：测灵魂-问卷列表
     * 接口路径：GET/testSoul
     */
    @GetMapping
    public ResponseEntity<Object> testSoulList() {
        log.info("测灵魂-问卷列表");
        return testSoulService.testSoulList();
    }

    /**
     * 接口名称：测灵魂-提交问卷
     * 接口路径：POST/testSoul
     */
    @PostMapping
    public ResponseEntity<Object> addAnswers(@RequestBody Map<String, List<Answer>> map) {
        log.info("提交问卷");
        List<Answer> answerList = map.get("answers");
        return testSoulService.addAnswers(answerList);
    }

    /**
     * 接口名称：测灵魂-查看结果
     * 接口路径：GET/testSoul/report/:id
     */
    @GetMapping("/report/{id}")
    public ResponseEntity<Object> findReport(@PathVariable("id") String reportId) {
        log.info("查看结果");
        return testSoulService.findReport(reportId);
    }


}
