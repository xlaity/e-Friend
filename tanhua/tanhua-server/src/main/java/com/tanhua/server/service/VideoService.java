package com.tanhua.server.service;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.VideoVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class VideoService {

    @Reference
    private VideoApi videoApi;

    @Reference
    private UserInfoApi userInfoApi;

    @Autowired
    private OssTemplate ossTemplate;

    // 注入fastdfs文件上传客户端对象
    @Autowired
    private FastFileStorageClient storageClient;

    // 注入web服务器对象, 主要用来获取上传地址
    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private VideoMqService videoMqService;

    /**
     *冯伟鑫（修改：增加视频点赞标志）
     * 接口名称：小视频列表
     */
    public PageResult queryVideoList(Integer page, Integer pagesize) {

        // 1、调用api分页查询小视频---------------------------------------------------------
        PageResult pageResult = this.findRecommend(page, pagesize, UserHolder.getUserId());

        if(pageResult == null) {
            pageResult = videoApi.findByPage(page,pagesize);
        }

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

                String key = "followUser_" + UserHolder.getUserId() + "_" + video.getUserId();
                if (redisTemplate.hasKey(key)) {
                    vo.setHasFocus(1);  // 如果存在关注标记，设置为1
                } else {
                    vo.setHasFocus(0);
                }

                String likeKey = "public_like_video_" + UserHolder.getUserId() + "_" + video.getId();
                if(redisTemplate.hasKey(likeKey)){
                    vo.setHasLiked(1);
                }else {
                    vo.setHasLiked(0);
                }


                vo.setHasLiked(0);

                // 2.3 添加vo到集合
                voList.add(vo);
            }
        }

        // 3.将volist设置到pageResult
        pageResult.setItems(voList);

        return pageResult;
    }


    public PageResult findRecommend(Integer page, Integer pagesize, Long userId) {

        PageResult result = null;

        String key = "QUANZI_VIDEO_RECOMMEND_" + userId;

        String value = redisTemplate.opsForValue().get(key);

        if (StringUtils.isEmpty(value)) {
            return null;
        }
        String[] vids = value.split(",");

        int startIndex = (page - 1) * pagesize;

        if (startIndex < vids.length) {

            int endIndex = startIndex + pagesize - 1;
            if (endIndex >= vids.length) {
                endIndex = vids.length - 1;
            }

            List<Long> vidList = new ArrayList<>();
            for (int i = startIndex; i <= endIndex; i++) {
                vidList.add(Long.valueOf(vids[i]));
            }
            List<Video> videoList = videoApi.findByVids(vidList);
            result = new PageResult(page, pagesize, vids.length, videoList);
        }

        return result;
    }


    /**
     * 接口名称：视频上传
     */
    @CacheEvict(value = "videoList", allEntries = true)
    public ResponseEntity<Object> uploadVideos(MultipartFile videoThumbnail, MultipartFile videoFile,
                                               String text) throws Exception {
        // 1.上传封面到阿里云oss
        String picUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());

        // 2.上传小视频到fastDFS
        String fileName = videoFile.getOriginalFilename();
        // 获取文件扩展名
        String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
        StorePath storePath = storageClient.uploadFile(videoFile.getInputStream(), videoFile.getSize(),
                fileExtName, null);
        // 获取小视频访问url
        String videoUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();

        // 3.创建小视频，调用服务提供者api保存数据
        Video video = new Video();
        video.setUserId(UserHolder.getUserId());
        if (StringUtils.isEmpty(text)) {
            video.setText("左手右手一个慢动作~");
        } else {
            video.setText(text);
        }
        video.setPicUrl(picUrl);
        video.setVideoUrl(videoUrl);
        video.setCreated(System.currentTimeMillis());

        video.setId(ObjectId.get());  // 手动生成一个id
        videoApi.save(video);

        // 【发送消息至RocketMQ-评分】
        videoMqService.videoMsg(video.getId().toString());

        return ResponseEntity.ok(null);
    }

    /**
     * 接口名称：视频用户关注
     */
    public ResponseEntity<Object> followUser(Long uid) {

        // 1.创建对象
        FollowUser followUser = new FollowUser();
        followUser.setCreated(System.currentTimeMillis());
        followUser.setUserId(UserHolder.getUserId());
        followUser.setFollowUserId(uid);

        // 2.调用服务提供者api保存数据
        videoApi.followUser(followUser);

        // 3.设置用户关注的标记到redis
        String key = "followUser_" + UserHolder.getUserId() + "_" + followUser.getFollowUserId();
        redisTemplate.opsForValue().set(key, "1");

        return ResponseEntity.ok(null);
    }
    /**
     * 接口名称：视频用户关注 - 取消
     */
    public ResponseEntity<Object> unfollowUser(Long uid) {
        // 1.调用服务提供者api删除关注数据
        videoApi.unfollowUser(UserHolder.getUserId(), uid);

        // 2.删除redis中的关注标记
        String key = "followUser_" + UserHolder.getUserId() + "_" + uid;
        redisTemplate.delete(key);

        return ResponseEntity.ok(null);
    }
}
