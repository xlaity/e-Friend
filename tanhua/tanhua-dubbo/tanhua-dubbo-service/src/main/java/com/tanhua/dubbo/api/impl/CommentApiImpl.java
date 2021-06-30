package com.tanhua.dubbo.api.impl;

import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.CommentApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;


@Service(timeout = 1000000)
public class CommentApiImpl implements CommentApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public long save(Comment comment) {
        // 1.往评论表插入评论数据
        mongoTemplate.save(comment);

        // 2.修改动态表的点赞数/喜欢数
        // 2.1 修改的条件
        Query query = new Query(Criteria.where("_id").is(comment.getPublishId()));
        // 2.2 修改的内容
        Update update = new Update();
        // 点赞数/喜欢数+1
        update.inc(comment.getCol(), 1);
        mongoTemplate.updateFirst(query, update, Publish.class);

        // 3.返回点赞/喜欢数量
        Query countQuery = new Query(Criteria.where("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType()));

        long count = mongoTemplate.count(countQuery, Comment.class);

        return count;
    }

    @Override
    public long delete(Comment comment) {
        // 1.删除评论表数据，根据动态id、评论类型（点赞/喜欢）、用户id删除评论数据
        Query removeQuery = new Query(Criteria.where("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType()).and("userId").is(comment.getUserId())
        );
        mongoTemplate.remove(removeQuery, Comment.class);

        // 2.修改动态表的点赞数、喜欢
        // 2.1 修改的条件
        Query query = new Query(Criteria.where("_id").is(comment.getPublishId()));
        // 2.2 修改的内容
        Update update = new Update();
        // 点赞数/喜欢数 -1
        update.inc(comment.getCol(), -1);
        mongoTemplate.updateFirst(query, update, Publish.class);

        // 3.返回点赞/喜欢数量
        Query countQuery = new Query(Criteria.where("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType()));

        long count = mongoTemplate.count(countQuery, Comment.class);

        return count;
    }

    @Override
    public PageResult queryCommentsList(String movementId, Integer page, Integer pagesize) {
        // 创建查询对象
        Query query = new Query(Criteria.where("publishId").is(new ObjectId(movementId)));
        // 指定排序字段
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        // 指定分页参数
        query.limit(pagesize).skip((page - 1) * pagesize);

        List<Comment> commentList = mongoTemplate.find(query, Comment.class);
        long count = mongoTemplate.count(query, Comment.class);
        return new PageResult(page, pagesize, (int) count, commentList);
    }

    @Override
    public PageResult findCommentsByUserId(Long userId, Integer commentType, Integer page, Integer pagesize) {
        // 注意：这里的条件userId是发布者的用户id，而Comment表中只存储了评论人id
        // 此时，如果根据发布者的id查询评论就无法实现了。
        // 所以：quanzi_comment评论表中要添加一个字段publishUserId。实体类也要加一个对应属性

        Query query = new Query(
                Criteria.where("commentType").is(commentType)
                        .and("publishUserId").is(userId)
        );
        List<Comment> commentList = mongoTemplate.find(query, Comment.class);
        long count = mongoTemplate.count(query, Comment.class);
        return new PageResult(page,pagesize, (int) count,commentList);
    }
}
