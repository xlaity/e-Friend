package com.tanhua.server.controller;

import com.tanhua.server.interceptor.UserHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/huanxin")
public class HuanXinController {

    /**
     * 接口名称：环信用户信息
     * 接口路径：GET/huanxin/user
     */
    @GetMapping("user")
    public ResponseEntity<Object> huanxinUser(){
        Map<String, String> map = new HashMap<>();
        map.put("username", UserHolder.getUserId().toString());
        map.put("password", "123456");
        return ResponseEntity.ok(map);
    }
}
