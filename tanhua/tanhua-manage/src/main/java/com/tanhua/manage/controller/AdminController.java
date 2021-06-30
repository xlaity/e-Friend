package com.tanhua.manage.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.tanhua.manage.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("system/users")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 接口名称：用户登录验证码图片
     * 接口路径：GET/system/users/verification
     */
    @GetMapping("verification")
    public void verification(String uuid, HttpServletResponse response) throws Exception{
        // 1、设置响应头
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        // 响应数据格式
        response.setContentType("image/jpeg");

        // 2、调用hutool工具类，生成验证码图片
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(299, 97);
        // 3、获取验证码文字
        String code = circleCaptcha.getCode();
        // 4、存入redis
        adminService.saveCap(uuid, code);
        // 5、写入响应流
        circleCaptcha.write(response.getOutputStream());
    }

    /**
     * 接口名称：用户登录
     * 接口路径：POST/system/users/login
     */
    @PostMapping("login")
    public ResponseEntity<Object> login(@RequestBody Map<String, Object> map){
        return adminService.login(map);
    }

    /**
     * 接口名称：用户基本信息
     * 接口路径：POST/system/users/profile
     */
    @PostMapping("profile")
    public ResponseEntity<Object> getUserInfo(){
        return adminService.getUserInfo();
    }

    /**
     * 接口名称：用户登出
     * 接口路径：POST/system/users/logout
     */
    @PostMapping("logout")
    public ResponseEntity<Object> logout(@RequestHeader("Authorization") String token){
        token = token.replace("Bearer ", "");
        return adminService.logout(token);
    }

}
