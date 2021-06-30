package com.tanhua.server.test;

import com.alibaba.fastjson.JSON;
import com.tanhua.commons.templates.HuanXinTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.dubbo.api.UserApi;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HuanxinTest {

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Reference
    private UserApi userApi;
    
    /**
     * 参考环信官网服务端集成，用户体系集成，生成token信息；
     * 为什么要生成token？
     * 1、生成token
     * 2、项目要连上环信，需要携带token参数
     */
    @Test
    public void testGetToken(){

        // 创建远程访问的工具类，支持rest请求
        RestTemplate restTemplate = new RestTemplate();

        // 参数1-远程访问的链接地址
        String url = "http://a1.easemob.com/1124210104046145/demo/token";
        // 参数2-创建一个map对象，封装请求的数据（参考官网）
        Map<String,String> map = new HashMap<>();
        map.put("grant_type", "client_credentials"); // 固定写死 
        map.put("client_id", "YXA6D3iKYg76TA-2n4T8wa6yrg");
        map.put("client_secret", "YXA6IX1j7MvZVSvxuewaO8GwHl7GvNc");

        // 参数1-url，参数2-请求参数（自动转json）,参数3-响应结果类型
        ResponseEntity<String> entity = restTemplate.postForEntity(url, map, String.class);

        // 获取响应的数据 {"access_token":"","application":"expires_in":5184000}
        String body = entity.getBody();
        System.out.println("响应的数据 = " + body);

        // 获取token
        Map<String, String> resultMap = JSON.parseObject(body, Map.class);
        System.out.println("最终获取到token = " + resultMap.get("access_token"));
    }

    /**
     * 测试注册环信用户
     */
    @Test
    public void testRegister(){
        huanXinTemplate.register(1l);
    }

    /**
     * 注册旧用户
     */
    @Test
    public void registerOldUser(){
        List<User> userList = userApi.findAll();
        for (User user : userList) {
            huanXinTemplate.register(user.getId());
        }
    }
}