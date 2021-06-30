package com.tanhua.server.controller;

import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据手机号码查询用户
     *
     * @param mobile
     * @return
     */
    @GetMapping("/findByMobile")
    public ResponseEntity<Object> findByMobile(String mobile) {
        int i = 1/0;
        return userService.findByMobile(mobile);
    }

    /**
     * 保存用户
     *
     * @param user
     * @return
     */
    @PostMapping
    public ResponseEntity<Object> save(@RequestBody User user) {
        return userService.save(user);
    }

    /**
     * 接口名称：登录第一步---手机号登录
     * 接口路径：POST/user/login
     */
    @PostMapping("login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> map){
        log.info("接口名称：登录第一步---手机号登录");
        String phone = map.get("phone");
        return userService.login(phone);
    }

    /**
     * 接口名称：登录第二步---验证码校验
     * 接口路径：POST/user/loginVerification
     */
    @PostMapping("loginVerification")
    public ResponseEntity<Object> loginVerification(@RequestBody Map<String, String> map){
        log.info("接口名称：登录第二步---验证码校验");
        String phone = map.get("phone");
        String verificationCode = map.get("verificationCode");
        return userService.loginVerification(phone, verificationCode);
    }

    /**
     * 接口名称：新用户---1填写资料
     * 接口路径：POST/user/loginReginfo
     */
    @PostMapping("loginReginfo")
    public ResponseEntity<Object> loginReginfo(@RequestBody UserInfo userInfo){

        log.info("接口名称：新用户---1填写资料");
        return userService.loginReginfo(userInfo);
    }


    /**
     * 接口名称：新用户---2选取头像
     * 接口路径：POST/user/loginReginfo/head
     */
    @PostMapping("/loginReginfo/head")
    public ResponseEntity<Object> updateUserHead(MultipartFile headPhoto) throws Exception {
        log.info("接口名称：新用户---2选取头像");
        return userService.updateUserHead(headPhoto);
    }
}
