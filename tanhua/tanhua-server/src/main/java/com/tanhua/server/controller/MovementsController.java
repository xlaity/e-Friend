package com.tanhua.server.controller;

import com.tanhua.domain.mongo.Publish;
import com.tanhua.server.service.MovementsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 圈子控制器
 */
@RestController
@RequestMapping("/movements")
@Slf4j
public class MovementsController {

    @Autowired
    private MovementsService movementsService;

    /**
     * 接口名称：动态-发布
     * 接口路径：POST/movements
     */
    @PostMapping
    public ResponseEntity<Object> saveMovements(Publish publish, MultipartFile[] imageContent) throws Exception {
        log.info("接口名称：动态-发布");
        return movementsService.saveMovements(publish, imageContent);
    }

    /**
     * 接口名称：好友动态
     * 接口路径：GET/movements
     */
    @GetMapping
    public ResponseEntity<Object> queryPublishList(@RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "5") Integer pagesize) throws Exception {
        log.info("接口名称：好友动态");
        return movementsService.queryPublishList(page, pagesize);
    }

    /**
     * 接口名称：推荐动态
     * 接口路径：GET/movements/recommend
     */
    @GetMapping("recommend")
    public ResponseEntity<Object> queryRecommendPublishList(@RequestParam(defaultValue = "1") Integer page,
                                                            @RequestParam(defaultValue = "5") Integer pagesize) throws Exception {
        log.info("接口名称：推荐动态");
        return movementsService.queryRecommendPublishList(page, pagesize);
    }

    /**
     * 接口名称：用户动态
     * 接口路径：GET/movements/all
     */
    @GetMapping("all")
    public ResponseEntity<Object> queryMyPublishList(@RequestParam(defaultValue = "1") Integer page,
                                                     @RequestParam(defaultValue = "5") Integer pagesize,
                                                     Long userId) {
        log.info("接口名称：用户动态");
        return movementsService.queryMyPublishList(page, pagesize, userId);
    }

    /**
     * 接口名称：动态-点赞
     * 接口路径：GET/movements/:id/like
     */
    @GetMapping("{id}/like")
    public ResponseEntity<Object> likeComment(@PathVariable String id) {
        log.info("接口名称：动态-点赞");
        return movementsService.likeComment(id);
    }

    /**
     * 接口名称：动态-取消点赞
     * 接口路径：GET/movements/:id/dislike
     */
    @GetMapping("{id}/dislike")
    public ResponseEntity<Object> dislikeComment(@PathVariable String id) {
        log.info("接口名称：动态-取消点赞");
        return movementsService.dislikeComment(id);
    }

    /**
     * 接口名称：动态-喜欢
     * 接口路径：GET/movements/:id/love
     */
    @GetMapping("{id}/love")
    public ResponseEntity<Object> loveComment(@PathVariable String id) {
        log.info("接口名称：动态-喜欢，参数 = " + id);
        return movementsService.loveComment(id);
    }

    /**
     * 接口名称：动态-取消喜欢
     * 接口路径：GET/movements/:id/unlove
     */
    @GetMapping("{id}/unlove")
    public ResponseEntity<Object> unloveComment(@PathVariable String id) {
        log.info("接口名称：动态-取消喜欢");
        return movementsService.unloveComment(id);
    }

    /**
     * 接口名称：单条动态
     * 接口路径：GET/movements/:id
     */
    @GetMapping("{id}")
    public ResponseEntity<Object> queryMovementsById(@PathVariable String id) {
        log.info("接口名称：单条动态");
        return movementsService.queryMovementsById(id);
    }

    /**
     * 接口名称：谁看过我
     * 接口路径：GET/movements/visitors
     */
    @GetMapping("/visitors")
    public ResponseEntity<Object> queryVisitors() {
        return movementsService.queryVisitors();
    }
}
