package com.tanhua.dubbo.test;

import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
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
public class CommentTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void updateOldData() {
        // 1.查询所有评论
        List<Comment> commentList = mongoTemplate.findAll(Comment.class);

        for (Comment comment : commentList) {
            // 2.根据评论中的动态id查询动态表
            Publish publish = mongoTemplate.findById(comment.getPublishId(), Publish.class);
            if (publish != null) {
                // 3.设置评论表的publishUserId
                Query query = new Query(Criteria.where("_id").is(comment.getId()));
                Update update = new Update();
                // 设置发布动态的userId
                update.set("publishUserId", publish.getUserId());
                mongoTemplate.updateFirst(query, update, Comment.class);
            }
        }
    }
}
