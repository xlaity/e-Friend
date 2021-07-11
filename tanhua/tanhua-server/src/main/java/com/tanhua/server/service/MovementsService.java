package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.domain.db.Ops;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.Visitors;
import com.tanhua.domain.vo.MovementsVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.VisitorsVo;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.PublishApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VisitorsApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.RelativeDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MovementsService {

    @Autowired
    private OssTemplate ossTemplate;

    @Reference
    private PublishApi publishApi;

    @Reference
    private UserInfoApi userInfoApi;

    @Reference
    private CommentApi commentApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Reference
    private VisitorsApi visitorsApi;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private MovementsMQService movementsMQService;

    /**
     * 接口名称：动态-发布
     */
    public ResponseEntity<Object> saveMovements(Publish publish, MultipartFile[] imageContent) throws Exception {

        String key = "FREEZE_"+UserHolder.getUserId();
        if(redisTemplate.hasKey(key)){
            //获得该缓存信息剩余生存时间
            Long time = redisTemplate.opsForValue().getOperations().getExpire(key);
            String timeString = RelativeDateFormat.millisecondsConvertToDHMS(time * 1000);
            //获取缓存值，格式化成Ops对象
            String value = redisTemplate.opsForValue().get(key);
            Ops ops = JSON.parseObject(value, Ops.class);
            //判断冻结范围是否是发布动态
            if (ops.getFreezingRange() == 3) {
                //判断冻结时间
                if (ops.getFreezingTime() == 1) {
                    return ResponseEntity.status(400).body("你已被禁止发布动态3天\n原因：" + ops.getReasonsForFreezing() + "\n剩余：" + timeString);
                } else if (ops.getFreezingTime() == 2) {
                    return ResponseEntity.status(400).body("你已被禁止发布动态七天\n原因：" + ops.getReasonsForFreezing() + "\n剩余：" + timeString);
                } else {
                    return ResponseEntity.status(400).body("你已被永久禁止发布动态\n原因：" + ops.getReasonsForFreezing());
                }
            }
        }


        // 1.上传图片信息到OSS
        List<String> urlList = new ArrayList<>();
        if (imageContent != null) {
            for (MultipartFile multipartFile : imageContent) {
                String url = ossTemplate.upload(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
                urlList.add(url);
            }
        }

        // 2.设置publish相关属性信息
        publish.setUserId(UserHolder.getUserId());
        publish.setMedias(urlList);
        publish.setCreated(System.currentTimeMillis());

        // 3.调用服务提供者api保存动态信息
        publish.setId(ObjectId.get());  // 手动设置id
        publish.setState(0);  // 设置动态状态默认是未审核
        publishApi.save(publish);

        // 【发送消息至RocketMQ-审核动态】
        rocketMQTemplate.convertAndSend("tanhua-publish", publish.getId().toString());

        // 【发送消息至RocketMQ-计算评分数据】
        movementsMQService.publishMsg(publish.getId().toString());

        return ResponseEntity.ok(null);
    }

    /**
     * 接口名称：好友动态
     */
    public ResponseEntity<Object> queryPublishList(Integer page, Integer pagesize) {
        // 1.获取登录用户id
        Long userId = UserHolder.getUserId();

        // 2.调用服务提供者分页查询该用户好友的动态信息
        PageResult pageResult = publishApi.findByTimeLine(page, pagesize, userId);

        // 3.调用公共方法，设置返回结果
        setMovementsVo(pageResult);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 接口名称：推荐动态
     */
    public ResponseEntity<Object> queryRecommendPublishList(Integer page, Integer pagesize) {
        // 1.获取登录用户id
        Long userId = UserHolder.getUserId();

        // 2.先查询redis中的推荐数据
        PageResult pageResult = findByRecommend(userId, page, pagesize);

        if(pageResult == null){
            // 3.调用服务提供者分页查询该用户的推荐动态
            pageResult = publishApi.queryRecommendPublishList(page, pagesize, userId);
        }

        // 3.调用公共方法，设置返回结果
        setMovementsVo(pageResult);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询redis中用户的推荐动态
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    private PageResult findByRecommend(Long userId, Integer page, Integer pagesize) {
        // 1.拼接推荐动态key
        String key = "QUANZI_PUBLISH_RECOMMEND_" + userId;

        // 2.查询redis中的推荐数据，10,20,30,40,50,60,70,80,90
        String value = redisTemplate.opsForValue().get(key);

        // 3.判断如果数据为空，返回null
        if(StringUtils.isEmpty(value)){
            return null;
        }

        // 4.将value转成数组
        String[] pids = value.split(",");

        // 获取开始下标，如果查询第一页，page = 1, 每页显示三条，pagesize = 3
        int startIndex = (page - 1) * pagesize;

        if(startIndex < pids.length){

            // startIndex = 0, pagesize = 3, endIndex = 2
            int endIndex = startIndex + pagesize - 1;

            if(endIndex >= pids.length){
                endIndex = pids.length - 1;
            }

            // 获取要查询的该页的动态id集合
            List<Long> pidList = new ArrayList<>();
            for(int i = startIndex; i<= endIndex; i++){
                pidList.add(Long.valueOf(pids[i]));
            }
            // 根据pid集合批量查询动态
            List<Publish> publishList = publishApi.findByPids(pidList);
            return new PageResult(page, pagesize, pids.length, publishList);

        }
        return null;
    }

    /**
     * 接口名称：用户动态
     */
    public ResponseEntity<Object> queryMyPublishList(Integer page, Integer pagesize, Long userId) {
        // 1.获取登录用户id
        if (userId == null) {
            // 如果未传入userId，就获取当前用户id
            userId = UserHolder.getUserId();
        }

        // 2.调用服务提供者分页查询该用户的个人动态
        PageResult pageResult = publishApi.queryMyPublishList(page, pagesize, userId);

        // 3.调用公共方法，设置返回结果
        setMovementsVo(pageResult);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 好友动态及推荐动态公共方法
     */
    public void setMovementsVo(PageResult pageResult) {

        // 获取当前页的数据
        List<Publish> publishList = (List<Publish>) pageResult.getItems();

        // 3.封装返回结果
        List<MovementsVo> voList = new ArrayList<>();

        if (publishList != null) {
            for (Publish publish : publishList) {
                // 3.1 创建vo对象
                MovementsVo vo = new MovementsVo();
                // 3.2 查询用户详情信息
                UserInfo userInfo = userInfoApi.findById(publish.getUserId());
                // 3.3 封装用户信息
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo, vo);
                    if (userInfo.getTags() != null) {
                        vo.setTags(userInfo.getTags().split(","));
                    }
                }

                // 3.4 封装动态信息
                BeanUtils.copyProperties(publish, vo);
                if (publish.getMedias() != null) {
                    vo.setImageContent(publish.getMedias().toArray(new String[]{}));
                }

                // 3.5 封装其他参数
                vo.setDistance("50米");
                vo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated()))); // 转换时间（1小时之前）
                vo.setId(publish.getId().toString());

                String likeKey = "public_like_comment_" + UserHolder.getUserId() + "_" + publish.getId();
                if (redisTemplate.hasKey(likeKey)) {
                    vo.setHasLiked(1); // 如果redis存在标记，代表当前用户对动态点赞过
                } else {
                    vo.setHasLiked(0);
                }

                String loveKey = "public_love_comment_" + UserHolder.getUserId() + "_" + publish.getId();
                if (redisTemplate.hasKey(loveKey)) {
                    vo.setHasLoved(1);// 如果redis存在标记，代表当前用户对动态喜欢过
                } else {
                    vo.setHasLoved(0);
                }


                // 3.6 添加vo对象
                voList.add(vo);
            }
        }
        pageResult.setItems(voList);
    }

    /**
     * 冯伟鑫（修改：comment需要给publishUserId字段传值，否则消息列表无法正常显示）
     * 接口名称：动态-点赞
     */
    public ResponseEntity<Object> likeComment(String id) {
        // 1.创建评论对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(id));
        comment.setCommentType(1);
        comment.setPubType(1);
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());

        //根据动态id查询发布动态者id
        Publish publish = publishApi.findById(id);
        comment.setPublishUserId(publish.getUserId());

        // 2.调用服务提供者api操作mongo, 往评论表插入数据、修改动态表的点赞数，并返回点赞数
        long count = commentApi.save(comment);

        // 3.存储点赞的标记到redis中
        String key = "public_like_comment_" + UserHolder.getUserId() + "_" + id;
        redisTemplate.opsForValue().set(key, "xxx");

        // 【发送消息至MQ-计算评分数据】
        movementsMQService.likePublishMsg(id);

        return ResponseEntity.ok(count);
    }

    /**
     * 接口名称：动态-取消点赞
     */
    public ResponseEntity<Object> dislikeComment(String id) {
        // 1.创建评论对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(id));
        comment.setCommentType(1);
        comment.setPubType(1);
        comment.setUserId(UserHolder.getUserId());

        // 2.调用服务提供者api操作mongo, 删除评论表数据，修改动态表点赞数，返回点赞数
        long count = commentApi.delete(comment);

        // 3.删除点赞的标记
        String key = "public_like_comment_" + UserHolder.getUserId() + "_" + id;
        redisTemplate.delete(key);

        // 【发送消息至MQ-计算评分数据】
        movementsMQService.disLikePublishMsg(id);

        return ResponseEntity.ok(count);
    }

    /**
     * 接口名称：动态-喜欢
     */
    public ResponseEntity<Object> loveComment(String id) {

        // 1.创建评论对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(id));
        comment.setCommentType(3);
        comment.setPubType(1);
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());

        //根据动态id查询发布动态者id
        Publish publish = publishApi.findById(id);
        comment.setPublishUserId(publish.getUserId());

        // 2.调用服务提供者api操作mongo, 往评论表插入数据、修改动态表的喜欢数，并返回喜欢数
        long count = commentApi.save(comment);

        // 3.存储喜欢的标记到redis中
        String key = "public_love_comment_" + UserHolder.getUserId() + "_" + id;
        redisTemplate.opsForValue().set(key, "xxx");

        // 【发送消息至MQ-计算评分数据】
        movementsMQService.lovePublishMsg(id);

        return ResponseEntity.ok(count);
    }

    /**
     * 接口名称：动态-取消喜欢
     */
    public ResponseEntity<Object> unloveComment(String id) {
        // 1.创建评论对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(id));
        comment.setCommentType(3);
        comment.setPubType(1);
        comment.setUserId(UserHolder.getUserId());

        // 2.调用服务提供者api操作mongo, 删除评论表数据，修改动态表喜欢数，返回喜欢数
        long count = commentApi.delete(comment);

        // 3.删除喜欢的标记
        String key = "public_love_comment_" + UserHolder.getUserId() + "_" + id;
        redisTemplate.delete(key);

        // 【发送消息至MQ-计算评分数据】
        movementsMQService.disLovePublishMsg(id);

        return ResponseEntity.ok(count);
    }

    /**
     * 接口名称：单条动态
     */
    public ResponseEntity<Object> queryMovementsById(String id) {

        // 1.查询服务提供者api，获取动态内容
        Publish publish = publishApi.findById(id);

        // 2.封装返回结果
        // 2.1 创建vo对象
        MovementsVo vo = new MovementsVo();
        // 2.2 查询用户详情信息
        UserInfo userInfo = userInfoApi.findById(publish.getUserId());
        // 2.3 封装用户信息
        if (userInfo != null) {
            BeanUtils.copyProperties(userInfo, vo);
            if (userInfo.getTags() != null) {
                vo.setTags(userInfo.getTags().split(","));
            }
        }

        // 2.4 封装动态信息
        BeanUtils.copyProperties(publish, vo);
        if (publish.getMedias() != null) {
            vo.setImageContent(publish.getMedias().toArray(new String[]{}));
        }

        // 2.5 封装其他参数
        vo.setDistance("50米");
        vo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated()))); // 转换时间（1小时之前）
        vo.setId(publish.getId().toString());

        String likeKey = "public_like_comment_" + UserHolder.getUserId() + "_" + publish.getId();
        if (redisTemplate.hasKey(likeKey)) {
            vo.setHasLiked(1); // 如果redis存在标记，代表当前用户对动态点赞过
        } else {
            vo.setHasLiked(0);
        }

        String loveKey = "public_love_comment_" + UserHolder.getUserId() + "_" + publish.getId();
        if (redisTemplate.hasKey(loveKey)) {
            vo.setHasLoved(1);// 如果redis存在标记，代表当前用户对动态喜欢过
        } else {
            vo.setHasLoved(0);
        }

        return ResponseEntity.ok(vo);
    }

    /**
     * 接口名称：谁看过我
     */
    public ResponseEntity<Object> queryVisitors() {

        // 1.查询redis是否存在最近访问时间，如果不存在说明是第一次访问
        String key = "visitors_time_" + UserHolder.getUserId();
        String lastTime = redisTemplate.opsForValue().get(key);

        List<Visitors> visitorsList = null;

        if (lastTime == null) {
            // 第一次访问，访问前5条
            visitorsList = visitorsApi.queryVisitors(UserHolder.getUserId(), 5);
        } else {
            // 不是第一次访问
            Long time = Long.parseLong(lastTime);
            visitorsList = visitorsApi.queryVisitors(UserHolder.getUserId(), time);
        }

        // 2.记录本次访问的时间到redis
        redisTemplate.opsForValue().set(key, System.currentTimeMillis() + "");

        // 3.封装返回结果
        List<VisitorsVo> voList = new ArrayList<>();

        if (visitorsList != null) {
            for (Visitors visitors : visitorsList) {
                // 3.1 创建vo对象
                VisitorsVo vo = new VisitorsVo();

                // 3.2 封装vo
                UserInfo userInfo = userInfoApi.findById(visitors.getVisitorUserId());
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo, vo);
                    if(userInfo.getTags() !=null){
                        vo.setTags(userInfo.getTags().split(","));
                    }
                }
                // 封装缘分值
                vo.setFateValue(visitors.getScore().intValue());

                // 3.3 添加vo
                voList.add(vo);
            }
        }
        return ResponseEntity.ok(voList);
    }
}
