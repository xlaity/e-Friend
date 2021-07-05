package com.tanhua.dubbo.api.impl;

import com.tanhua.domain.mongo.UserSum;
import com.tanhua.dubbo.api.UserSumApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @program: tanhua-group9
 * @create: 2021-07-03 12:18
 * 查询并且修改浏览次数数据
 **/
/**
 * 江杰
 * @Params:
 * @Return
 */
@Service
public class UserSumApiImpl implements UserSumApi {
    @Autowired
    private MongoTemplate mongoTemplate;
    //根据userid查询用户能够获取语音的次数
    @Override
    public UserSum findOne(Long id) {
        Query query = new Query(Criteria.where("userId").is(id));
        UserSum one = mongoTemplate.findOne(query, UserSum.class);
        return one;
    }
    /**
     * 江杰
     * @Params:
     * @Return
     */
    //修改用户可获取语音次数
    @Override
    public void update(Long id) {
        Query qeury = new Query(Criteria.where("userId").is(id));
        Update update = new Update();
        update.inc("userNum",-1);
        mongoTemplate.updateFirst(qeury,update,UserSum.class);
    }
    /**
     * 江杰
     * @Params:
     * @Return
     */
    //次日更新所有用户次数信息
    @Override
    public void updateTimes() {
        Query query = new Query();
        Update update = new Update();
        update.set("userNum",10);
        mongoTemplate.updateFirst(query,update,UserSum.class);
    }

    //保存数据
    @Override
    public void save(UserSum userSum1) {
        mongoTemplate.save(userSum1);
    }
}
