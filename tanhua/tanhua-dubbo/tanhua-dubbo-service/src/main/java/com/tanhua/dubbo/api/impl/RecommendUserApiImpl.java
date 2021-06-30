package com.tanhua.dubbo.api.impl;


import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.RecommendUserApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendUserApiImpl implements RecommendUserApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * db.recommend_user.find({userId:1}).sort({score:-1}).limit(1)
     */
    @Override
    public RecommendUser queryWithMaxScore(Long userId) {
        // 创建查询对象
        Query query = new Query(Criteria.where("userId").is(userId));
        // 指定排序字段
        query.with(Sort.by(Sort.Direction.DESC, "score"));
        // 指定返回一条数据
        query.limit(1);

        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        return recommendUser;
    }

    @Override
    public PageResult queryRecommendation(Long userId, Integer page, Integer pagesize) {
        // 创建查询对象
        Query query = new Query(Criteria.where("userId").is(userId));
        // 指定排序字段
        query.with(Sort.by(Sort.Direction.DESC, "score"));
        // 指定分页参数
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 查询当前页的数据
        List<RecommendUser> recommendUserList = mongoTemplate.find(query, RecommendUser.class);

        // 查询总条数
        long count = mongoTemplate.count(query, RecommendUser.class);

        return new PageResult(page, pagesize, (int) count, recommendUserList);
    }

    @Override
    public long queryScore(Long userId, Long recommendUserId) {

        Query query = new Query(Criteria.where("userId").is(userId)
                .and("recommendUserId").is(recommendUserId));
        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);

        if (recommendUser == null) {
            return 80l;
        }
        return recommendUser.getScore().longValue();
    }


}
