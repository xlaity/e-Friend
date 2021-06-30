package com.tanhua.dubbo.api.impl;
import com.tanhua.domain.vo.PageResult;
import org.bson.types.ObjectId;


import com.tanhua.domain.mongo.Friend;
import com.tanhua.dubbo.api.FriendApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class FriendApiImpl implements FriendApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Long userId, long friendId) {
        // 判断数据是否存在
        Query query = new Query(Criteria.where("userId").is(userId)
                .and("friendId").is(friendId));

        // 如果数据不存在，插入数据
        if(!mongoTemplate.exists(query, Friend.class)){
            Friend friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(friendId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }


        // 判断数据是否存在
        Query query2 = new Query(Criteria.where("userId").is(friendId)
                .and("friendId").is(userId));

        // 如果数据不存在，插入数据
        if(!mongoTemplate.exists(query2, Friend.class)){
            Friend friend = new Friend();
            friend.setUserId(friendId);
            friend.setFriendId(userId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }

    }

    /**
     * db.tanhua_users.find({userId:1}).sort({created: -1}).limit(10).skip(0)
     */
    @Override
    public PageResult queryContractList(Integer page, Integer pagesize, String keyword, Long userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page - 1) * pagesize);
        List<Friend> friendList = mongoTemplate.find(query, Friend.class);
        long count = mongoTemplate.count(query, Friend.class);
        return new PageResult(page, pagesize, (int) count, friendList);
    }
}
