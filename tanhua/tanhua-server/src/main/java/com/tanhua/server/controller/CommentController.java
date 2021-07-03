package com.tanhua.server.controller;

import com.tanhua.server.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/comments")
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 接口名称：评论列表
     * 接口路径：GET/comments
     */
    @GetMapping
    public ResponseEntity<Object> queryCommentsList(String movementId,
                                                    @RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer pagesize) {
        log.info("接口名称：评论列表");
        return commentService.queryCommentsList(movementId, page, pagesize);
    }

    /**
     * 冯伟鑫（有改动，增加参数标志对动态还是视频进行评论）
     * 接口名称：评论-提交
     * 接口路径：POST/comments
     */
    @PostMapping
    public ResponseEntity<Object> saveComments(@RequestBody Map<String, String> map) {
        log.info("接口名称：评论-提交");
        String movementId = map.get("movementId");
        String comment = map.get("comment");
        return commentService.saveComments(movementId, comment,1);
    }

    /*
    * 冯伟鑫（增加）
    * 接口名称：评论-点赞
    * 接口路径：GET/comments/:id/like
    */
    @GetMapping("{id}/like")
    public ResponseEntity<Object> likeContent(@PathVariable("id") String id){
        return commentService.Content(id,1);
    }


    /*
    * 冯伟鑫（增加）
    * 接口名称：评论-取消点赞
    * 接口路径：GET/comments/:id/dislike
    */
    @GetMapping("{id}/dislike")
    public ResponseEntity disLikeContent(@PathVariable("id") String id){
        return commentService.Content(id,0);
    }
}
