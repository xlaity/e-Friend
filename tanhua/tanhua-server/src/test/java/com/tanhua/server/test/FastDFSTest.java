package com.tanhua.server.test;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FastDFSTest {

    // 注入fastdfs文件上传客户端对象
    @Autowired
    private FastFileStorageClient storageClient;

    // 注入web服务器对象, 主要用来获取上传地址
    @Autowired
    private FdfsWebServer fdfsWebServer;


    /**
     * 测试fastdfs
     */
    @Test
    public void testFastDfs() throws Exception{
        String fileName = "D:\\14.jpg";
        File file = new File(fileName);
        // 参数1-输入流，参数2-文件的大小，参数3-文件扩展名
        StorePath storePath = storageClient.uploadFile(new FileInputStream(fileName), file.length(),
                "jpg", null);
        // 获取文件存储路径
        String fullPath = storePath.getFullPath();
        System.out.println("fullPath = " + fullPath);

        // 最终文件的下载地址
        String filePath = fdfsWebServer.getWebServerUrl() + fullPath;
        System.out.println("filePath = " + filePath);
    }


}
