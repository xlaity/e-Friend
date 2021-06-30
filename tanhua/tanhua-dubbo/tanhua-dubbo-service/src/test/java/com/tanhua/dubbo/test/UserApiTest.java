package com.tanhua.dubbo.test;
import java.util.Date;

import com.tanhua.domain.db.User;
import com.tanhua.dubbo.api.UserApi;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试服务提供者api
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserApiTest {

    /**
     * 测试dubbo服务：
     * 1、在dubbo服务工程中，测试dubbo服务，注入dubbo接口代理对象，使用@Autowired注入
     * 2、在服务消费者工程中注入dubbo服务使用@Reference
     * 3、注意：运行当前测试类时候，不要启动dubbo服务，否则报错。
     * 4、如果注入的userApi对象报红，可能是idea校验的问题，不用理会。
     */
    @Autowired
    private UserApi userApi;

    /**
     * 测试保存用户
     */
    @Test
    public void testSave() {
        User user = new User();
        user.setMobile("18600001111");
        user.setPassword("888");
        user.setCreated(new Date());
        user.setUpdated(new Date());

        userApi.save(user);
    }

    /**
     * 根据手机号码查询
     */
    @Test
    public void testFindByMobile(){
        User user = userApi.findByMobile("18600001111");
        System.out.println("user = " + user);
    }
}
