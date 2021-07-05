package com.tanhua.server.controller;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.RecommendQueryVo;
import com.tanhua.server.service.TodayBestService;
import com.tanhua.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 今日佳人控制器
 */
@RestController
@RequestMapping("/tanhua")
@Slf4j
public class TodayBestController {

    @Autowired
    private TodayBestService todayBestService;

    @Autowired
    private UserService userService;

    /**
     * 接口名称：今日佳人
     * 接口路径：GET/tanhua/todayBest
     */
    @GetMapping("todayBest")
    public ResponseEntity<Object> todayBest(){
        log.info("接口名称：今日佳人");
        return todayBestService.todayBest();
    }

    /**
     * 接口名称：推荐朋友
     * 接口路径：GET/tanhua/recommendation
     */
    @GetMapping("recommendation")
    public ResponseEntity<Object> recommendation(RecommendQueryVo recommendQueryVo){
        log.info("接口名称：推荐朋友");
        return todayBestService.queryRecommendation(recommendQueryVo);
    }

    /**
     * 接口名称：佳人信息
     * 接口路径：GET/tanhua/:id/personalInfo
     */
    @GetMapping("{id}/personalInfo")
    public ResponseEntity<Object> queryPersonalInfo(@PathVariable("id") Long recommendUserId){
        log.info("接口名称：佳人信息");
        return todayBestService.queryPersonalInfo(recommendUserId);
    }

    /**
     * 接口名称：查询陌生人问题
     * 接口路径：GET/tanhua/strangerQuestions
     */
    @GetMapping("strangerQuestions")
    public ResponseEntity<Object> strangerQuestions(Long userId){
        log.info("接口名称：查询陌生人问题");
        return todayBestService.strangerQuestions(userId);
    }

    /**
     * 接口名称：回复陌生人问题
     * 接口路径：POST/tanhua/strangerQuestions
     */
    @PostMapping("strangerQuestions")
    public ResponseEntity<Object> replyQuestions(@RequestBody Map<String, Object> map){
        Integer userId = (Integer) map.get("userId");
        String reply = (String) map.get("reply");
        return todayBestService.replyQuestions(userId, reply);
    }

    /**
     * 接口名称：搜附近
     * 接口路径：GET/tanhua/search
     */
    @GetMapping("search")
    public ResponseEntity<Object> searchNear(String gender, Long distance){
        return todayBestService.searchNear(gender, distance);
    }
    //桃花传音-喜欢
    @GetMapping("{id}/love")
    public ResponseEntity<Object> love(@PathVariable("id") Long id){
        System.out.println(id);
        return userService.love(id);
    }
    //桃花传音-不喜欢
    @GetMapping("{id}/unlove")
    public ResponseEntity<Object> unlove(@PathVariable("id") Long id){
        System.out.println(id);
        return userService.unlove(id);
    }
}
