package com.tanhua.server.test.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheTest {

    @Autowired
    private UserInfoTestService userInfoTestService;

    @Test
    public void findAll() {
        System.out.println(userInfoTestService.findAll());
    }


    @Test
    public void saveOrDelete() {
        userInfoTestService.saveOrDelete();
    }

    @Test
    public void findById() {
        // 测试：自定义key，向缓存存放数据
        System.out.println(userInfoTestService.findById(2L));
    }
}