package com.tanhua.manage.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.Ops;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.*;
import com.tanhua.dubbo.api.*;
import com.tanhua.manage.utils.RelativeDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

@Service
public class UserService {

    @Reference
    private UserInfoApi userInfoApi;

    @Reference
    private VideoApi videoApi;

    @Reference
    private PublishApi publishApi;

    @Reference
    private CommentApi commentApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Reference
    private OpsApi opsApi;

    public ResponseEntity<Object> findByPage(Integer page, Integer pagesize) {
        // 1.调用服务提供者分页查询用户列表
        Page<UserInfo> result = userInfoApi.findByPage(page, pagesize);

        ArrayList<UserInfoVo> userInfoVos = new ArrayList<>();
        List<UserInfo> userInfoList = result.getRecords();
        if(userInfoList!=null){
            for (UserInfo userInfo : userInfoList) {
                UserInfoVo userInfoVo = new UserInfoVo();
                BeanUtils.copyProperties(userInfo,userInfoVo);

                //判断缓存中是否存在冻结标志，封装返回冻结状态码
                String key = "FREEZE_"+userInfo.getId();
                if(redisTemplate.hasKey(key)){
                    userInfoVo.setUserStatus("2");
                }else {
                    userInfoVo.setUserStatus("1");
                }

                userInfoVos.add(userInfoVo);
            }
        }
        // 2.封装返回结果
        PageResult pageResult = new PageResult(page, pagesize, (int) result.getTotal(), userInfoVos);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 接口名称：用户基本资料
     */
    public ResponseEntity<Object> findById(Long userId) {
        UserInfoVo userInfoVo = new UserInfoVo();
        UserInfo userInfo = userInfoApi.findById(userId);
        BeanUtils.copyProperties(userInfo,userInfoVo);

        //判断缓存中是否存在冻结标志，封装返回冻结状态码
        String key = "FREEZE_"+userInfo.getId();
        if(redisTemplate.hasKey(key)){
            userInfoVo.setUserStatus("2");
        }else {
            userInfoVo.setUserStatus("1");
        }

        return ResponseEntity.ok(userInfoVo);
    }

    /**
     * 接口名称：视频记录翻页
     */
    public ResponseEntity<Object> findVideosList(Integer page, Integer pagesize, Long uid) {
        // 1.调用服务提供者分页查询视频列表
        PageResult pageResult = videoApi.findVideosList(page, pagesize, uid);

        // 2.封装返回结果
        // 获取当前页的数据
        List<Video> videoList = (List<Video>) pageResult.getItems();

        // 2.封装返回结果
        List<VideoVo> voList = new ArrayList<>();
        if (videoList != null) {
            for (Video video : videoList) {
                // 2.1 创建vo对象
                VideoVo vo = new VideoVo();

                // 2.2 封装数据
                // 封装小视频信息
                BeanUtils.copyProperties(video, vo);

                // 封装用户信息
                UserInfo userInfo = userInfoApi.findById(video.getUserId());
                if (userInfo != null) {
                    vo.setAvatar(userInfo.getAvatar());
                    vo.setNickname(userInfo.getNickname());
                }

                // 封装其他信息
                vo.setId(video.getId().toString());
                vo.setCover(video.getPicUrl());
                vo.setSignature(video.getText());

                // 2.3 添加vo到集合
                voList.add(vo);
            }
        }

        // 3.将volist设置到pageResult
        pageResult.setItems(voList);

        return ResponseEntity.ok(pageResult);
    }


    /**
     * lwh
     * 接口名称：动态分页
     */
    public ResponseEntity<Object> findMovementsList(Integer page, Integer pagesize, Long uid, Long state) {
        //查询单个用户动态
        if (uid != null) {
            // 1.调用服务提供者分页查询该用户的个人动态
            PageResult pageResult = publishApi.queryMyPublishList(page, pagesize, uid);

            // 2.调用公共方法，设置返回结果
            setMovementsVo(pageResult);

            return ResponseEntity.ok(pageResult);
        } else if (state != null) {

            // 1.调用服务提供者分页查询不同的审核状态
            PageResult pageResult = publishApi.findByState(page, pagesize, state);

            // 2.调用公共方法，设置返回结果
            setMovementsVo(pageResult);

            return ResponseEntity.ok(pageResult);

        }

        // 1.调用服务提供者分页查询所有动态
        PageResult pageResult = publishApi.finByAll(page, pagesize);

        // 2.调用公共方法，设置返回结果
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
                vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(publish.getCreated())));
                vo.setId(publish.getId().toString());

                /**
                 * lwh
                 */

                if(publish.getState()==0){
                    vo.setState("3");
                }else if(publish.getState()==1){
                    vo.setState("5");
                }else if(publish.getState()==2){
                    vo.setState("4");
                }


                // 3.6 添加vo对象
                voList.add(vo);
            }
        }
        pageResult.setItems(voList);

    }

    /**
     * 动态详情
     */
    public ResponseEntity<Object> findMovementsById(String publishId) {
        // 1.查询服务提供者api，获取动态内容
        Publish publish = publishApi.findById(publishId);

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
        vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(publish.getCreated()))); // 转换时间（1小时之前）
        vo.setId(publish.getId().toString());

        return ResponseEntity.ok(vo);

    }

    /**
     * 动态的评论列表
     */
    public ResponseEntity<Object> findCommentsById(String publishId, Integer page, Integer pagesize) {
        // 1.查询服务提供者api，查询评论列表
        PageResult pageResult = commentApi.queryCommentsList(publishId, page, pagesize);

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
                vo.setHasLiked(0);

                // 2.3 添加vo到集合
                voList.add(vo);
            }
        }
        pageResult.setItems(voList);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * lwh
     * 动态通过
     */
    public ResponseEntity<Object> updatePass(List<String> publishIdList) {
        String message = "false";
        //遍历修改审核状态信息
        if (publishIdList.size() != 0) {
            for (String publishId : publishIdList) {
                publishApi.updateState(publishId, 1);
            }
            message = "true";
        }

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("message", message);
        return ResponseEntity.ok(resultMap);
    }


    /**
     * lwh
     * 接口名称：动态拒绝
     */
    public ResponseEntity<Object> updateReject(List<String> publishIdList) {
        String message = "false";
        //遍历修改审核状态信息
        if (publishIdList.size() != 0) {
            for (String publishId : publishIdList) {
                publishApi.updateState(publishId, 2);
            }
            message = "true";
        }

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("message", message);
        return ResponseEntity.ok(resultMap);

    }



    /**
     * lwh
     * 接口名称：动态撤销
     */
    public ResponseEntity<Object> revocation(List<String> publishIdList) {
        String message = "false";
        //遍历修改审核状态信息
        if (publishIdList.size() != 0) {
            for (String publishId : publishIdList) {
                publishApi.updateState(publishId, 0);
            }
            message = "true";
        }

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("message", message);
        return ResponseEntity.ok(resultMap);
    }


    /**
     * ydj
     * 用户冻结
     */
    public ResponseEntity<Object> accountFreeze(Ops ops) {
        //存储冻结信息
        opsApi.accountFreeze(ops);

        //把冻结信息保存在缓存中
        String key = "FREEZE_"+ops.getUserId();
        switch (ops.getFreezingTime()){
            case 1:
                redisTemplate.opsForValue().set(key, JSON.toJSONString(ops), Duration.ofDays(3));

                break;
            case 2:
                redisTemplate.opsForValue().set(key,JSON.toJSONString(ops), Duration.ofDays(7));
                break;
            case 3:
                redisTemplate.opsForValue().set(key,JSON.toJSONString(ops));
                break;
            default:
                break;
        }

        Map<String, String> map = new HashMap<>();
        map.put("message","操作成功");
        return ResponseEntity.ok(map);
    }


    /**
     * ydj
     * 用户解冻
     */
    public ResponseEntity<Object> accountUnFreeze(Integer userId, String reasonsForThawing) {
        //从缓存中删除冻结信息
        String key = "FREEZE_"+userId;
        redisTemplate.delete(key);

        Map<String, String> map = new HashMap<>();
        map.put("message","操作成功");
        return ResponseEntity.ok(map);
    }
}
