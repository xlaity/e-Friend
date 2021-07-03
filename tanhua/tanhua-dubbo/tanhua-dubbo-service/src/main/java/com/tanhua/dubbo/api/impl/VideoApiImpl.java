package com.tanhua.dubbo.api.impl;

import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.UserLike;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.dubbo.utils.IdService;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class VideoApiImpl implements VideoApi {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IdService idService;

    @Override
    public PageResult findByPage(Integer page, Integer pagesize) {
        Query query = new Query();
        // 指定排序参数
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        // 指定分页参数
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 获取当前页数据
        List<Video> videoList = mongoTemplate.find(query, Video.class);
        // 获取总条数
        long count = mongoTemplate.count(query, Video.class);

        return new PageResult(page, pagesize, (int) count, videoList);
    }

    @Override
    public void save(Video video) {
        // 【设置vid，推荐系统需要】
        video.setVid(idService.getNextId("video"));
        mongoTemplate.save(video);
    }

    /*
     * 冯伟鑫（修改：增强列表联动）
     * 视频用户关注
     * */
    @Override
    public void followUser(FollowUser followUser) {

        //添加关注信息
        mongoTemplate.save(followUser);

        Query query = new Query(Criteria.where("userId").is(followUser.getFollowUserId()).and("likeUserId").is(followUser.getUserId()));
        //彼此喜欢删除粉丝信息，添加朋友信息
        if(mongoTemplate.exists(query, UserLike.class)){
            mongoTemplate.remove(query,UserLike.class);

            Friend friend = new Friend();
            friend.setUserId(followUser.getUserId());
            friend.setFriendId(followUser.getFollowUserId());
            friend.setCreated(followUser.getCreated());
            mongoTemplate.save(friend);

            Friend friend1 = new Friend();
            friend1.setUserId(followUser.getFollowUserId());
            friend1.setFriendId(followUser.getUserId());
            friend1.setCreated(followUser.getCreated());
            mongoTemplate.save(friend1);
        }else {
            //单方喜欢插入喜欢信息
            UserLike userLike = new UserLike();
            userLike.setUserId(followUser.getUserId());
            userLike.setLikeUserId(followUser.getFollowUserId());
            userLike.setCreated(followUser.getCreated());

            mongoTemplate.save(userLike);
        }
    }

    /*
     * 冯伟鑫（修改：增强列表联动）
     * 取消视频用户关注
     * */
    @Override
    public void unfollowUser(Long userId, Long uid) {
        //删除关注信息
        Query query = new Query(Criteria.where("userId").is(userId).and("followUserId").is(uid));
        mongoTemplate.remove(query,FollowUser.class);

        Query query1 = new Query(Criteria.where("userId").is(userId).and("friendId").is(uid));
        //取消关注时是朋友则删除朋友信息，插入喜欢信息
        if(mongoTemplate.exists(query1,Friend.class)){
            mongoTemplate.remove(query1,Friend.class);

            Query query2 = new Query(Criteria.where("userId").is(uid).and("friendId").is(userId));
            mongoTemplate.remove(query2,Friend.class);

            UserLike userLike = new UserLike();
            userLike.setUserId(uid);
            userLike.setLikeUserId(userId);
            userLike.setCreated(System.currentTimeMillis());
            mongoTemplate.save(userLike);
        }else {
            //单方喜欢删除喜欢信息
            Query query2 = new Query(Criteria.where("userId").is(userId).and("likeUserId").is(uid));
            mongoTemplate.remove(query2,UserLike.class);
        }
    }

    @Override
    public PageResult findVideosList(Integer page, Integer pagesize, Long uid) {
        Query query = new Query(Criteria.where("userId").is(uid));
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page - 1) * pagesize);

        List<Video> videoList = mongoTemplate.find(query, Video.class);
        long count = mongoTemplate.count(query, Video.class);

        return new PageResult(page, pagesize, (int) count, videoList);
    }

    @Override
    public Video findById(String videoId) {
        Video video = mongoTemplate.findById(new ObjectId(videoId), Video.class);
        return video;
    }

    @Override
    public List<Video> findByVids(List<Long> vidList) {
        Query query = new Query(Criteria.where("vid").in(vidList));
        List<Video> videoList = mongoTemplate.find(query, Video.class);
        return videoList;
    }

}
