package com.tanhua.dubbo.api.impl;

import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.UserLike;
import com.tanhua.domain.mongo.Visitors;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserLikeApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserLikeApiImpl implements UserLikeApi {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RecommendUserApi recommendUserApi;

    /**
     * db.tanhua_users.count({userId:1})
     */
    public Long queryEachLoveCount(Long userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        long count = mongoTemplate.count(query, Friend.class);
        return count;
    }

    /**
     * db.user_like.count({userId:1})
     */
    public Long queryLoveCount(Long userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        long count = mongoTemplate.count(query, UserLike.class);
        return count;
    }

    /**
     * db.user_like.count({likeUserId:1})
     */
    public Long queryFanCount(Long userId) {
        Query query = new Query(Criteria.where("likeUserId").is(userId));
        long count = mongoTemplate.count(query, UserLike.class);
        return count;
    }

    @Override
    public PageResult queryEachLoveList(Long userId, Integer page, Integer pagesize) {
        Query query = new Query(Criteria.where("userId").is(userId));
        // 指定排序参数
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        // 指定分页参数
        query.limit(pagesize).skip((page -1) * pagesize);

        List<Friend> friendList = mongoTemplate.find(query, Friend.class);
        long count = mongoTemplate.count(query, Friend.class);

        // 将当前页数据转为List<Map>
        List<Map<String, Object>> mapList = new ArrayList<>();

        if (friendList != null) {
            for (Friend friend : friendList) {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", friend.getFriendId()); // 设置好友id到返回结果中
                map.put("score", recommendUserApi.queryScore(userId, friend.getFriendId()));

                mapList.add(map);
            }
        }
        return new PageResult(page, pagesize, (int) count, mapList);
    }

    @Override
    public PageResult queryLoveList(Long userId, Integer page, Integer pagesize) {
        Query query = new Query(Criteria.where("userId").is(userId));
        // 指定排序参数
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        // 指定分页参数
        query.limit(pagesize).skip((page -1) * pagesize);

        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        long count = mongoTemplate.count(query, UserLike.class);
        // 将当前页数据转为List<Map>
        List<Map<String, Object>> mapList = new ArrayList<>();

        if (userLikeList != null) {
            for (UserLike userLike : userLikeList) {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", userLike.getLikeUserId()); // 设置喜欢的用户id到返回结果中
                map.put("score", recommendUserApi.queryScore(userId, userLike.getLikeUserId()));

                mapList.add(map);
            }
        }
        return new PageResult(page, pagesize, (int) count, mapList);
    }

    @Override
    public PageResult queryFanList(Long userId, Integer page, Integer pagesize) {
        Query query = new Query(Criteria.where("likeUserId").is(userId));
        // 指定排序参数
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page -1) * pagesize);

        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        long count = mongoTemplate.count(query, UserLike.class);
        // 将当前页数据转为List<Map>
        List<Map<String, Object>> mapList = new ArrayList<>();

        if (userLikeList != null) {
            for (UserLike userLike : userLikeList) {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", userLike.getUserId()); // 设置粉丝的用户id到返回结果中
                map.put("score", recommendUserApi.queryScore(userId, userLike.getUserId()));

                mapList.add(map);
            }
        }
        return new PageResult(page, pagesize, (int) count, mapList);
    }

    @Override
    public PageResult queryVisitorList(Long userId, Integer page, Integer pagesize) {
        Query query = new Query(Criteria.where("userId").is(userId));
        // 指定排序参数
        query.with(Sort.by(Sort.Direction.DESC, "date"));
        // 指定分页参数
        query.limit(pagesize).skip((page -1) * pagesize);

        List<Visitors> visitorsList = mongoTemplate.find(query, Visitors.class);

        long count = mongoTemplate.count(query, Visitors.class);

        // 将当前页数据转为List<Map>
        List<Map<String, Object>> mapList = new ArrayList<>();

        if (visitorsList != null) {
            for (Visitors visitors : visitorsList) {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", visitors.getVisitorUserId()); // 设置访客的用户id到返回结果中
                map.put("score", recommendUserApi.queryScore(userId, visitors.getVisitorUserId()));

                mapList.add(map);
            }
        }
        return new PageResult(page, pagesize, (int) count, mapList);
    }

    /**
     * db.user_like.remove({userId:3, likeUserId:20})
     */
    public void delete(Long userId, Long uid) {
        Query query = new Query(Criteria.where("userId").is(uid)
                .and("likeUserId").is(userId));
        mongoTemplate.remove(query, UserLike.class);
    }

    /*
    * 冯伟鑫（增加：添加喜欢信息）
    * */
    @Override
    public void addFans(UserLike userLike) {
        mongoTemplate.save(userLike);
    }

    /*
     * 冯伟鑫（增加：删除喜欢信息）
     * */
    @Override
    public void deleteLike(Long userId, Long uid) {
        Query query = new Query(Criteria.where("userId").is(userId).and("likeUserId").is(uid));
        mongoTemplate.remove(query,UserLike.class);
    }

    /*
     * 冯伟鑫（增加：判断是否是粉丝）
     * */
    @Override
    public boolean isFans(Long userId, Long likeUserId) {
        Query query = new Query(Criteria.where("userId").is(likeUserId).and("likeUserId").is(userId));
        return mongoTemplate.exists(query,UserLike.class);
    }
}
