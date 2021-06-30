package com.tanhua.dubbo.api.impl;

import com.tanhua.domain.mongo.Visitors;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.VisitorsApi;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class VisitorsApiImpl implements VisitorsApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    // 自己家的，不需要远程调用加reference
    @Autowired
    private RecommendUserApi recommendUserApi;


    /**
     * db.visitors.find({userId: 1}).sort({date: -1}).limit(5)
     */
    public List<Visitors> queryVisitors(Long userId, int count) {
        Query query = new Query(Criteria.where("userId").is(userId));
        // 指定排序参数
        query.with(Sort.by(Sort.Direction.DESC, "date"));
        // 指定返回条数
        query.limit(count);

        List<Visitors> visitorsList = mongoTemplate.find(query, Visitors.class);

        return getVisitors(visitorsList);
    }

    /**
     * db.visitors.find({userId: 1, date:{$gt:1614770370649}}).sort({date: -1})
     */
    public List<Visitors> queryVisitors(Long userId, Long time) {
        Query query = new Query(Criteria.where("userId").is(userId)
                    .and("date").gt(time));

        // 指定排序参数
        query.with(Sort.by(Sort.Direction.DESC, "date"));

        List<Visitors> visitorsList = mongoTemplate.find(query, Visitors.class);

        return getVisitors(visitorsList);
    }

    /**
     * 抽取公共方法
     * @param visitorsList
     * @return
     */
    private List<Visitors> getVisitors(List<Visitors> visitorsList){
        if (visitorsList != null) {
            for (Visitors visitors : visitorsList) {
                // 查询每个访客的缘分值
                long fateValue = recommendUserApi.queryScore(visitors.getUserId(), visitors.getVisitorUserId());
                visitors.setScore(fateValue);
            }
        }
        return visitorsList;
    }

}
