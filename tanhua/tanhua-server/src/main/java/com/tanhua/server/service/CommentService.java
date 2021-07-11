package com.tanhua.server.service;


import com.alibaba.fastjson.JSON;
import com.tanhua.domain.db.Ops;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.CommentVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.PublishApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.RelativeDateFormat;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Reference
    private CommentApi commentApi;

    @Reference
    private UserInfoApi userInfoApi;

    @Reference
    private PublishApi publishApi;

    @Reference
    private VideoApi videoApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private VideoMqService videoMqService;

    @Autowired
    private MovementsMQService movementsMQService;

    /**
     * 接口名称：评论列表
     */
    public ResponseEntity<Object> queryCommentsList(String movementId, Integer page, Integer pagesize) {

        // 1.查询服务提供者api，查询评论列表
        PageResult pageResult = commentApi.queryCommentsList(movementId, page, pagesize);

        // 获取当前页的内容
        List<Comment> commentList = (List<Comment>) pageResult.getItems();

        // 2.封装返回结果
        List<CommentVo> voList = new ArrayList<>();

        if (commentList != null) {
            for (Comment comment : commentList) {
                // 2.1 创建vo对象
                CommentVo vo = new CommentVo();

                // 2.2 封装数据
                UserInfo userInfo = userInfoApi.findById(comment.getUserId());
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo, vo);
                }

                vo.setId(comment.getId().toString());
                vo.setContent(comment.getContent());
                // 日期格式转换
                vo.setCreateDate(new DateTime(comment.getCreated()).toString("yyyy年MM月dd日 HH:mm"));
                vo.setLikeCount(comment.getLikeCount());

                String key = "like_" + UserHolder.getUserId() + "_" + comment.getPublishId();
                if(redisTemplate.hasKey(key)){
                    vo.setHasLiked(1);
                }else {
                    vo.setHasLiked(0);
                }

                // 2.3 添加vo到集合
                voList.add(vo);
            }
        }
        pageResult.setItems(voList);
        return ResponseEntity.ok(pageResult);

    }

    /**
     * 冯伟鑫（修改：增加参数标志对动态还是视频评论）
     * 接口名称：评论-提交
     */
    public ResponseEntity<Object> saveComments(String movementId, String content,Integer pubType) {
        String key = "FREEZE_"+UserHolder.getUserId();
        if(redisTemplate.hasKey(key)){
            //获得该缓存信息剩余生存时间
            Long time = redisTemplate.opsForValue().getOperations().getExpire(key);
            String timeString = RelativeDateFormat.millisecondsConvertToDHMS(time * 1000);
            //获取缓存值，格式化成Ops对象
            String value = redisTemplate.opsForValue().get(key);
            Ops ops = JSON.parseObject(value, Ops.class);
            //判断冻结范围是否是发表评论
            if (ops.getFreezingRange() == 2) {
                //判断冻结时间
                if (ops.getFreezingTime() == 1) {
                    return ResponseEntity.status(400).body("你已被禁止发言3天\n原因：" + ops.getReasonsForFreezing() + "\n剩余：" + timeString);
                } else if (ops.getFreezingTime() == 2) {
                    return ResponseEntity.status(400).body("你已被禁止发言七天\n原因：" + ops.getReasonsForFreezing() + "\n剩余：" + timeString);
                } else {
                    return ResponseEntity.status(400).body("你已被永久禁止发言\n原因：" + ops.getReasonsForFreezing());
                }
            }
        }

        // 1.创建评论对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(2);
        comment.setPubType(pubType);
        comment.setContent(content);
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());

        if(pubType == 1){
            //根据动态id查询发布动态者id（只有插入发布动态者id消息列表才能正常显示）
            Publish publish = publishApi.findById(movementId);
            comment.setPublishUserId(publish.getUserId());
            //【圈子相关操作计分】
            movementsMQService.commentPublishMsg(comment.getPublishId().toString());
        }else if(pubType == 2){
            //根据视频id查询发布动态者id
            Video video = videoApi.findById(movementId);
            comment.setPublishUserId(video.getUserId());
            //【视频相关操作计分】
            videoMqService.commentVideoMsg(comment.getPublishId().toString());
        }

        // 2.调用服务提供者api操作mongo, 往评论表插入数据、修改动态表的评论数
        commentApi.save(comment);

        return ResponseEntity.ok(null);
    }

    /**
     * 冯伟鑫（增加）
     * 接口名称：视频点赞
     */
    public ResponseEntity<Object> likeComment(String id) {
        Long userId = UserHolder.getUserId();
        // 1.创建评论对象
        Comment comment = new Comment();
        comment.setId(new ObjectId());
        comment.setPublishId(new ObjectId(id));
        comment.setUserId(userId);
        comment.setCommentType(1);
        comment.setPubType(2);
        comment.setCreated(System.currentTimeMillis());
        //根据视频id查询发布动态者id
        Video video = videoApi.findById(id);
        comment.setPublishUserId(video.getUserId());

        long count = commentApi.save(comment);

        // 保存哪一个用户对哪个视频进行点赞的记录
        String key = "public_like_video_" + UserHolder.getUserId() + "_" + id;
        redisTemplate.opsForValue().set(key, "have");

        //【视频相关操作计分】
        videoMqService.likeVideoMsg(comment.getPublishId().toString());

        return ResponseEntity.ok(count);
    }

    /**
     * 冯伟鑫（增加）
     * 接口名称：取消视频点赞
     */
    public ResponseEntity<Object> dislikeComment(String id) {
        Long userId = UserHolder.getUserId();
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(id));
        comment.setPubType(2);
        comment.setUserId(userId);
        comment.setCommentType(1);

        Long count = commentApi.delete(comment);

        //保存标志信息
        String key = "public_like_video_" + UserHolder.getUserId() + "_" + id;
        redisTemplate.delete(key);

        //【视频相关操作计分】
        videoMqService.disLikeVideoMsg(comment.getPublishId().toString());

        return ResponseEntity.ok(count);
    }

    /**
     * 冯伟鑫（增加）
     * 接口名称：对评论的点赞和取消点赞
     */
    public ResponseEntity<Object> Content(String id,Integer flag) {

        //直接修改评论表
        long count =  commentApi.updateComment(id,flag);

        String key = "like_" + UserHolder.getUserId() + "_" + id;
        if(flag==1){
            redisTemplate.opsForValue().set(key,"have");
        }else {
            redisTemplate.delete(key);
        }

        return ResponseEntity.ok(count);
    }
}
