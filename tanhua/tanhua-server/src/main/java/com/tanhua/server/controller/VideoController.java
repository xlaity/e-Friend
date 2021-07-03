package com.tanhua.server.controller;

import com.tanhua.domain.vo.PageResult;
import com.tanhua.server.service.CommentService;
import com.tanhua.server.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 小视频控制器
 */
@RestController
@Slf4j
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private CommentService commentService;

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


    /*
    * 冯伟鑫（增加）
    * 接口名称：视频点赞
    * 接口路径：POST/smallVideos/:id/like
    */
    @PostMapping("/smallVideos/{id}/like")
    public ResponseEntity<Object> likeVideo(@PathVariable String id){
        return commentService.likeComment(id);
    }


    /*
    * 冯伟鑫（增加）
    * 接口名称：视频点赞 - 取消
    * 接口路径：POST/smallVideos/:id/dislike
    */
    @PostMapping("/smallVideos/{id}/dislike")
    public ResponseEntity<Object> dislikeVideo(@PathVariable String id){
        return commentService.dislikeComment(id);
    }


    /*
    * 冯伟鑫（增加）
    * 接口名称：评论发布
    * 接口路径：POST/smallVideos/:id/comments
    */
    @PostMapping("/smallVideos/{id}/comments")
    public ResponseEntity<Object> videoComments(@PathVariable String id,@RequestBody Map<String,String> map){
        String comment = map.get("comment");
        //整数参数标明对动态、视频还是评论进行评论
        return commentService.saveComments(id,comment,2);
    }


    /*
    * 冯伟鑫（增加）
    * 接口名称：评论列表
    * 接口路径：GET/smallVideos/:id/comments
    */
    @GetMapping("/smallVideos/{id}/comments")
    public ResponseEntity<Object> queryComments(@PathVariable String id,Integer page,Integer pagesize){
        return commentService.queryCommentsList(id,page,pagesize);
    }
}
