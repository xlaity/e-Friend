package com.tanhua.manage.service;


import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tanhua.manage.domain.Admin;
import com.tanhua.manage.exception.BusinessException;
import com.tanhua.manage.interceptor.AdminHolder;
import com.tanhua.manage.mapper.AdminMapper;
import com.tanhua.manage.vo.AdminVo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService extends ServiceImpl<AdminMapper, Admin> {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AdminMapper adminMapper;

    @Value("${tanhua.secret}")
    private String secret;

    // token key前缀
    private final String MANAGE_TOKEN = "MANAGE_TOKEN_";

    /**
     * 验证码存入redis
     *
     * @param uuid 存储redis中的key
     * @param code 验证码
     */
    public void saveCap(String uuid, String code) {
        String key = "MANAGE_CAP_" + uuid;
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(2));
    }


    public ResponseEntity<Object> login(Map<String, Object> map) {
        // 1.获取请求参数
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        String verificationCode = (String) map.get("verificationCode");
        String uuid = (String) map.get("uuid");

        // 2.判断用户名密码
        // 2.1 是否为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new BusinessException("用户名或密码为空！");
        }

        // 2.2 判断用户名是否存在
        Admin admin = this.query().eq("username", username).one();
        if (admin == null) {
            throw new BusinessException("用户名不存在！");
        }

        // 2.3 判断密码是否正确
        if(!SecureUtil.md5(password).equals(admin.getPassword())){
            throw new BusinessException("用户密码不正确！");
        }

        // 3.判断验证码
        // 3.1 验证码参数非空判断
        if(StringUtils.isEmpty(verificationCode)){
            throw new BusinessException("验证码不能为空！");
        }

        // 3.2 校验验证码
        String key = "MANAGE_CAP_" + uuid;
        String codeInRedis = redisTemplate.opsForValue().get(key);
        if(codeInRedis == null || !codeInRedis.equals(verificationCode)){
            throw new BusinessException("验证码校验失败！");
        }

        // 4.删除验证码
        redisTemplate.delete(key);

        // 5.生成token
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", admin.getId());
        claimsMap.put("username", admin.getUsername());

        String token = Jwts.builder().setClaims(claimsMap)
                .signWith(SignatureAlgorithm.HS256, secret).compact();

        // 6.用户对象存入redis
        String adminStr = JSON.toJSONString(admin);
        redisTemplate.opsForValue().set(MANAGE_TOKEN + token, adminStr, Duration.ofHours(4));

        // 7.返回结果
        Map<String, String> result = new HashMap<>();
        result.put("token", token);

        return ResponseEntity.ok(result);
    }

    // 根据登录token，获取用户对象
    public Admin findUserByToken(String token) {
        String userStr = redisTemplate.opsForValue().get(MANAGE_TOKEN + token);
        if(userStr == null) {
            return null;
        }
        // 续期
        redisTemplate.opsForValue().set(MANAGE_TOKEN + token, userStr, Duration.ofHours(4));
        return JSON.parseObject(userStr, Admin.class);
    }

    /**
     * 接口名称：用户基本信息
     */
    public ResponseEntity<Object> getUserInfo() {
        Admin admin = AdminHolder.getAdmin();
        AdminVo vo = new AdminVo();
        BeanUtils.copyProperties(admin, vo);
        return ResponseEntity.ok(vo);
    }

    /**
     * 用户退出登录
     */
    public ResponseEntity<Object> logout(String token) {
        String key = MANAGE_TOKEN + token;
        redisTemplate.delete(key);
        return ResponseEntity.ok(null);
    }
}
