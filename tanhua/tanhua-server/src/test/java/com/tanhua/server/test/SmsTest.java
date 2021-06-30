package com.tanhua.server.test;

import com.tanhua.commons.templates.SmsTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试阿里云短信
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsTest {

    @Autowired
    private SmsTemplate smsTemplate;

    /**
     * 测试短信发送
     */
    @Test
    public void testSms(){
        smsTemplate.sendSms("13660605731", "123456");
    }

}
