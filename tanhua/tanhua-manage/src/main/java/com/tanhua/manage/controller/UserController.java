package com.tanhua.manage.controller;

import com.tanhua.manage.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("manage")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 接口名称：用户数据翻页
     * 接口路径：GET/manage/users
     */
    @GetMapping("users")
    public ResponseEntity<Object> findByPage(Integer page, Integer pagesize) {
        return userService.findByPage(page, pagesize);
    }

    /**
     * 接口名称：用户基本资料
     * 接口路径：GET/manage/users/:userID
     */
    @GetMapping("users/{userID}")
    public ResponseEntity<Object> findById(@PathVariable("userID") Long userId) {
        return userService.findById(userId);
    }

    /**
     * 接口名称：视频记录翻页
     * 接口路径：GET/manage/videos
     */
    @GetMapping("videos")
    public ResponseEntity<Object> findVideosList(Integer page,
                                                 Integer pagesize,
                                                 Long uid) {
        return userService.findVideosList(page, pagesize, uid);
    }

    /**
     * 接口名称：动态分页
     * 接口路径：GET/manage/messages
     */
    @GetMapping("messages")
    public ResponseEntity<Object> findMovementsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize, Long uid, String state) {
        if (StringUtils.isEmpty(state) || state.equals("''")) {
            return userService.findMovementsList(page, pagesize, uid, null);
        } else {
            return userService.findMovementsList(page, pagesize, uid, Long.valueOf(state));
        }
    }

    /**
     * 接口名称：动态详情
     * 接口路径：GET/manage/messages/:id
     */
    @GetMapping("/messages/{id}")
    public ResponseEntity<Object> findMovementsById(@PathVariable("id") String publishId) {
        return userService.findMovementsById(publishId);
    }

    /**
     * 接口名称：评论列表翻页
     * 接口路径：GET/manage/messages/comments
     */
    @GetMapping("/messages/comments")
    public ResponseEntity<Object> findCommentsById(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize,
            @RequestParam(name = "messageID") String publishId) {
        return userService.findCommentsById(publishId, page, pagesize);
    }

    /**
     * lwh
     * 接口名称：动态通过
     * 接口路径：POST/manage/messages/pass
     */
    @PostMapping("messages/pass")
    public ResponseEntity<Object> updatePass(@RequestBody List<String> publishIdList) {
        return userService.updatePass(publishIdList);
    }

    /**
     * 接口名称：动态拒绝
     * 接口路径：POST/manage/messages/reject
     */
    @PostMapping("messages/reject")
    public ResponseEntity<Object> updateReject(@RequestBody List<String> publishIdList) {
        return userService.updateReject(publishIdList);
    }


    /**
     * lwh
     *接口名称：动态审核撤销
     *接口路径：POST/manage/messages/revocation
     */
    @PostMapping("messages/revocation")
    public ResponseEntity<Object> revocation(@RequestBody List<String> publishIdList) {
        return userService.revocation(publishIdList);
    }
}
