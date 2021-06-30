package com.tanhua.server.controller;

import com.tanhua.server.service.IMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/messages")
public class IMController {

    @Autowired
    private IMService imService;

    /**
     * 接口名称：根据环信id查询用户信息
     * 接口路径：GET/messages/userinfo
     */
    @GetMapping("userinfo")
    public ResponseEntity<Object> getHuanxinUserInfo(Long huanxinId) {
        return imService.getHuanxinUserInfo(huanxinId);
    }

    /**
     * 接口名称：联系人添加
     * 接口路径：POST/messages/contacts
     */
    @PostMapping("contacts")
    public ResponseEntity<Object> addContact(@RequestBody Map<String, Integer> map) {
        Integer userId = map.get("userId");
        return imService.addContact(userId.longValue());
    }

    /**
     * 接口名称：联系人列表
     * 接口路径：GET/messages/contacts
     */
    @GetMapping("contacts")
    public ResponseEntity<Object> queryContractList(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer pagesize,
                                                    String keyword) {
        return imService.queryContractList(page, pagesize, keyword);
    }

    /**
     * 接口名称：点赞列表
     * 接口路径：GET/messages/likes
     */
    @GetMapping("likes")
    public ResponseEntity<Object> querylikesList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize) {
        return imService.querylikesList(1, page,pagesize);
    }
    /**
     * 接口名称：评论列表
     * 接口路径：GET/messages/comments
     */
    @GetMapping("comments")
    public ResponseEntity<Object> queryCommentsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize) {
        return imService.querylikesList(2, page,pagesize);
    }
    /**
     * 接口名称：喜欢列表
     * 接口路径：GET/messages/loves
     */
    @GetMapping("loves")
    public ResponseEntity<Object> queryLovesList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize) {
        return imService.querylikesList(3, page,pagesize);
    }
}
