package com.tanhua.server.controller;

import com.tanhua.domain.vo.PageResult;
import com.tanhua.server.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 小视频控制器
 */
@RestController
@Slf4j
public class VideoController {

    @Autowired
    private VideoService videoService;

    /**
     * 接口名称：小视频列表
     * 接口路径：GET/smallVideos
     */
    @GetMapping("smallVideos")
    public ResponseEntity<Object> queryVideoList(@RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer pagesize) {
        log.info("接口名称：小视频列表");
        PageResult pageResult = videoService.queryVideoList(page, pagesize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 接口名称：视频上传
     * 接口路径：POST/smallVideos
     */
    @PostMapping("smallVideos")
    public ResponseEntity<Object> uploadVideos(MultipartFile videoThumbnail,
                                               MultipartFile videoFile,
                                               String text) throws Exception {
        log.info("接口名称：视频上传");
        return videoService.uploadVideos(videoThumbnail, videoFile, text);
    }

    /**
     * 接口名称：视频用户关注
     * 接口路径：POST/smallVideos/:uid/userFocus
     */
    @PostMapping("/smallVideos/{uid}/userFocus")
    public ResponseEntity<Object> followUser(@PathVariable Long uid){
        log.info("接口名称：视频用户关注");
        return videoService.followUser(uid);
    }

    /**
     * 接口名称：视频用户关注 - 取消
     * 接口路径：POST/smallVideos/:uid/userUnFocus
     */
    @PostMapping("/smallVideos/{uid}/userUnFocus")
    public ResponseEntity<Object> unfollowUser(@PathVariable Long uid){
        log.info("接口名称：视频用户关注 - 取消");
        return videoService.unfollowUser(uid);
    }
}
