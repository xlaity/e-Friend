package com.tanhua.server.controller;

import com.tanhua.domain.db.Settings;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.SettingsService;
import com.tanhua.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 通用设置控制器
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private UserService userService;

    /**
     * 接口名称：修改手机号- 1 发送短信验证码
     * 接口路径：POST/users/phone/sendVerificationCode
     */
    @PostMapping("/phone/sendVerificationCode")
    public ResponseEntity<Object> sendVerificationCode(){
        log.info("接口名称：修改手机号- 1 发送短信验证码");
        // 获取登录用户手机号码，发送验证码
        String phone = UserHolder.get().getMobile();
        // 复用登录第一步的方法
        return userService.login(phone);
    }

    /**
     * 接口名称：修改手机号 - 2 校验验证码
     * 接口路径：POST/users/phone/checkVerificationCode
     */
    @PostMapping("/phone/checkVerificationCode")
    public ResponseEntity<Object> checkVerificationCode(@RequestBody Map<String,String> map){
        log.info("接口名称：修改手机号 - 2 校验验证码");
        String code = map.get("verificationCode");
        return userService.checkVerificationCode(code);
    }

    /**
     * 接口名称：修改手机号 - 3 保存
     * 接口路径：POST/users/phone
     */
    @PostMapping("/phone")
    public ResponseEntity<Object> phone(@RequestBody Map<String,String> map){
        String phone = map.get("phone");
        return userService.updateUserPhone(phone);
    }


    /**
     * 接口名称：用户通用设置 - 读取
     * 接口路径：GET/users/settings
     */
    @GetMapping("settings")
    public ResponseEntity<Object> settings(){
        log.info("接口名称：用户通用设置 - 读取");
        return settingsService.querySettings();
    }

    /**
     * 接口名称：通知设置 - 保存
     * 接口路径：POST/users/notifications/setting
     */
    @PostMapping("notifications/setting")
    public ResponseEntity<Object> saveNotification(@RequestBody Settings settings){
        log.info("接口名称：通知设置 - 保存");
        return settingsService.saveNotification(settings);
    }

    /**
     * 接口名称：设置陌生人问题 - 保存
     * 接口路径：POST/users/questions
     */
    @PostMapping("questions")
    public ResponseEntity<Object> saveQuestion(@RequestBody Map<String, String> map){
        log.info("接口名称：设置陌生人问题 - 保存");
        String content = map.get("content");
        return settingsService.saveQuestion(content);
    }

    /**
     * 接口名称：黑名单 - 翻页列表
     * 接口路径：GET/users/blacklist
     */
    @GetMapping("blacklist")
    public ResponseEntity<Object> blacklist(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "5") Integer pagesize){
        log.info("接口名称：黑名单 - 翻页列表");
        return settingsService.blacklist(page, pagesize);

    }

    /**
     * 接口名称：黑名单 - 移除
     * 接口路径：DELETE/users/blacklist/:uid
     */
    @DeleteMapping("blacklist/{uid}")
    public ResponseEntity<Object> deleteBlackUser(@PathVariable("uid") Long uid){
        log.info("接口名称：黑名单 - 移除");
        return settingsService.deleteBlackUser(uid);
    }
}
