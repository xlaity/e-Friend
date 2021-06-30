package com.tanhua.dubbo.test;

import com.tanhua.domain.db.User;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.PublishScore;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.mongo.VideoScore;
import com.tanhua.dubbo.mapper.UserMapper;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RecommendTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testPublishScoreData() {

        List<User> users = userMapper.selectList(null);

        List<Publish> list = mongoTemplate.find(new Query(), Publish.class);

        for (int i = 0; i < 1000; i++) {
            PublishScore score = new PublishScore();
            score.setId(ObjectId.get());
            score.setDate(System.currentTimeMillis());
            // 随机取某个动态
            Publish publish = list.get(new Random().nextInt(list.size()));
            score.setPublishId(publish.getPid());
            // 随机设置分数
            score.setScore(Double.valueOf(new Random().nextInt(10)));
            // 随机取某个用户
            User user = users.get(new Random().nextInt(5));
            score.setUserId(user.getId());

            mongoTemplate.save(score);
        }
    }

    @Test
    public void testVideosScoreData() {

        List<User> users = userMapper.selectList(null);

        List<Video> list = mongoTemplate.find(new Query(), Video.class);

        for (int i = 0; i < 1000; i++) {
            VideoScore score = new VideoScore();
            score.setId(ObjectId.get());
            score.setDate(System.currentTimeMillis());
            // 随机取某个视频
            Video video = list.get(new Random().nextInt(list.size()));
            score.setVideoId(video.getVid());
            // 随机设置分数
            score.setScore(Double.valueOf(new Random().nextInt(10)));
            // 随机取某个用户
            User user = users.get(new Random().nextInt(5));
            score.setUserId(user.getId());
            mongoTemplate.save(score);
        }

    }
}