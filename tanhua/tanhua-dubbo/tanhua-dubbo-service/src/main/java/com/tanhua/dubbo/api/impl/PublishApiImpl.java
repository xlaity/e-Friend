package com.tanhua.dubbo.api.impl;

import com.tanhua.domain.mongo.*;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.utils.IdService;
import org.bson.types.ObjectId;

import com.tanhua.dubbo.api.PublishApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;


@Service(timeout = 100000000)
public class PublishApiImpl implements PublishApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdService idService;


    @Override
    public void save(Publish publish) {
        // 1.往发布表插入动态信息
        // 【发布动态时设置pid，推荐系统需要】
        publish.setPid(idService.getNextId("quanzi_publish"));
        mongoTemplate.save(publish);

        // 2.往该用户的相册表中插入数据
        Album album = new Album();
        album.setPublishId(publish.getId());
        album.setCreated(publish.getCreated());
        mongoTemplate.save(album, "quanzi_album_" + publish.getUserId());

        // 3.往好友的时间线表中插入数据
        // 3.1 查询该用户的好友信息
        Query query = new Query(Criteria.where("userId").is(publish.getUserId()));
        List<Friend> friendList = mongoTemplate.find(query, Friend.class);

        if (friendList != null) {
            for (Friend friend : friendList) {
                // 往每个好友的时间线表中插入数据
                TimeLine timeLine = new TimeLine();
                timeLine.setUserId(publish.getUserId());
                timeLine.setPublishId(publish.getId());
                timeLine.setCreated(publish.getCreated());
                mongoTemplate.save(timeLine, "quanzi_time_line_" + friend.getFriendId());
            }
        }
    }

    @Override
    public PageResult findByTimeLine(Integer page, Integer pagesize, Long userId) {
        // 创建查询对象
        Query query = new Query();
        // 指定排序参数
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        // 指定分页参数
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 查询当前用户的时间线表
        List<TimeLine> timeLineList = mongoTemplate.find(query, TimeLine.class,
                "quanzi_time_line_" + userId);

        // 获取总条数
        long count = mongoTemplate.count(query, TimeLine.class, "quanzi_time_line_" + userId);

        List<Publish> publishList = new ArrayList<>();
        if (timeLineList != null) {
            for (TimeLine timeLine : timeLineList) {
                // 根据动态id查询发布动态表
                Publish publish = mongoTemplate.findById(timeLine.getPublishId(), Publish.class);
                if (publish != null) {
                    publishList.add(publish);
                }
            }
        }

        return new PageResult(page, pagesize, (int) count, publishList);
    }

    @Override
    public PageResult queryRecommendPublishList(Integer page, Integer pagesize, Long userId) {
        // 创建查询对象
        Query query = new Query(Criteria.where("userId").is(userId).and("state").is(1));
        // 指定排序参数
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        // 指定分页参数
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 查询推荐动态表
        List<RecommendQuanzi> recommendQuanziList = mongoTemplate.find(query, RecommendQuanzi.class);

        // 获取总条数
        long count = mongoTemplate.count(query, RecommendQuanzi.class);

        List<Publish> publishList = new ArrayList<>();
        if (recommendQuanziList != null) {
            for (RecommendQuanzi recommendQuanzi : recommendQuanziList) {
                // 根据动态id查询发布动态表
                Publish publish = mongoTemplate.findById(recommendQuanzi.getPublishId(), Publish.class);
                if (publish != null) {
                    publishList.add(publish);
                }
            }
        }

        return new PageResult(page, pagesize, (int) count, publishList);
    }

    @Override
    public PageResult queryMyPublishList(Integer page, Integer pagesize, Long userId) {
        // 创建查询对象
        Query query = new Query();
        // 指定排序参数
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        // 指定分页参数
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 查询当前用户的相册表
        List<Album> albumList = mongoTemplate.find(query, Album.class,"quanzi_album_" + userId);

        // 获取总条数
        long count = mongoTemplate.count(query, Album.class,"quanzi_album_" + userId);

        List<Publish> publishList = new ArrayList<>();
        if (albumList != null) {
            for (Album album : albumList) {
                // 根据动态id查询发布动态表
                Publish publish = mongoTemplate.findById(album.getPublishId(), Publish.class);
                if (publish != null) {
                    publishList.add(publish);
                }
            }
        }

        return new PageResult(page, pagesize, (int) count, publishList);
    }

    @Override
    public Publish findById(String id) {
        Publish publish = mongoTemplate.findById(new ObjectId(id), Publish.class);
        return publish;
    }

    @Override
    public void updateState(String publishId, Integer state) {
        // 查询条件
        Query query = new Query(Criteria.where("_id").is(new ObjectId(publishId)));
        // 设置修改后的内容
        Update update = new Update();
        update.set("state", state);
        mongoTemplate.updateFirst(query, update, Publish.class);
    }

    @Override
    public List<Publish> findByPids(List<Long> pidList) {
        Query query = new Query(Criteria.where("pid").in(pidList));
        List<Publish> publishList = mongoTemplate.find(query, Publish.class);
        return publishList;
    }
}
