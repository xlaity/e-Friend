package com.tanhua.server.test;

import com.tanhua.commons.templates.OssTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

/**
 * oss测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OssTest {
    @Autowired
    OssTemplate ossTemplate;

    /**
     * 测试文件上传
     */
    @Test
    public void testOss() throws Exception{
        String fileName = "D:\\2.jpg";
        File file = new File(fileName);
        String url = ossTemplate.upload(fileName, new FileInputStream(file));
        System.out.println("url = " + url);
    }

}
