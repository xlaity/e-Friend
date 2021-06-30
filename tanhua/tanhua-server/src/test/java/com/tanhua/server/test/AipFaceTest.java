package com.tanhua.server.test;

import com.tanhua.commons.templates.AipFaceTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.file.Files;

/**
 * 人脸检测测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AipFaceTest {

    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    /**
     * 测试人脸识别
     */
    @Test
    public void testDetectFace() throws Exception{
        String fileName = "E:\\1.jpg";
        File file = new File(fileName);
        boolean detect = aipFaceTemplate.detect(Files.readAllBytes(file.toPath()));
        System.out.println("是否人脸 = " + detect);
    }
}
