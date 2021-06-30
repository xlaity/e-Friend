package com.tanhua.dubbo.api.impl;

import com.tanhua.domain.mongo.FollowUser;
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

    @Override
    public void followUser(FollowUser followUser) {
        mongoTemplate.save(followUser);
    }

    @Override
    public void unfollowUser(Long userId, Long uid) {
        Query query = new Query(Criteria.where("userId").is(userId)
                .and("followUserId").is(uid)
        );
        mongoTemplate.remove(query, FollowUser.class);
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
