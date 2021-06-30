package com.tanhua.dubbo.utils;

import com.tanhua.domain.mongo.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * 获取自增长ID工具类
 */
@Component
public class IdService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 获取某个表的最新ID
     * @return
     */
    public Long getNextId(String collName) {
        // 查询条件
        Query query = new Query(Criteria.where("collName").is(collName));

        // 设置seqId + 1
        Update update = new Update();
        update.inc("seqId", 1);

        // 如果根据条件查询到了数据，就进行修改，否则新增
        mongoTemplate.upsert(query, update, Sequence.class);

        Sequence sequence = mongoTemplate.findOne(query, Sequence.class);

        return sequence.getSeqId();
    }
}