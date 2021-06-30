package com.tanhua.dubbo.test;

import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.Video;
import com.tanhua.dubbo.DubboServerApplication;
import com.tanhua.dubbo.utils.IdService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SequenceTest {

    @Autowired
    private IdService idService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void getSequence() {
        // 获取动态表最新的id
        Long pid = idService.getNextId("quanzi_publish");
        System.out.println("pid = " + pid);
    }

    @Test
    public void getSequence2() {
        Long vid = idService.getNextId("video");
        System.out.println("vid = " + vid);
    }

    /**
     * 设置publish中的pid字段
     */
    @Test
    public void setPid() {
        // 查询pid字段不存在的数据
        Query query = new Query(Criteria.where("pid").exists(false));
        List<Publish> publishList = mongoTemplate.find(query, Publish.class);

        if (publishList != null && publishList.size() > 0) {
            for (Publish publish : publishList) {
                Query query1 = new Query(Criteria.where("_id").is(publish.getId()));
                Update update = new Update();
                update.set("pid", idService.getNextId("quanzi_publish"));
                mongoTemplate.updateFirst(query1, update, Publish.class);
            }
        }
    }

    /**
     * 设置video中的vid字段
     */
    @Test
    public void setVid() {
        // 查询vid字段不存在的数据
        Query query = new Query(Criteria.where("vid").exists(false));
        List<Video> videoList = mongoTemplate.find(query, Video.class);

        if (videoList != null && videoList.size() > 0) {
            for (Video video : videoList) {
                Query query1 = new Query(Criteria.where("_id").is(video.getId()));
                Update update = new Update();
                update.set("vid", idService.getNextId("video"));
                mongoTemplate.updateFirst(query1, update, Video.class);
            }
        }
    }

}