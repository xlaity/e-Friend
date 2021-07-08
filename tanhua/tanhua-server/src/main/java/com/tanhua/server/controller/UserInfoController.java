package com.tanhua.server.controller;


import com.tanhua.domain.db.UserInfo;
import com.tanhua.server.service.TodayBestService;
import com.tanhua.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户详情控制器
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserInfoController {

    @Autowired
    private UserService userService;

    @Autowired
    private TodayBestService todayBestService;

    /**
     * 接口名称：用户资料 - 读取
     * 接口路径：GET/users
     */
    @GetMapping
    public ResponseEntity<Object> findUserInfoById(){
        log.info("接口名称：用户资料 - 读取");
        return userService.findUserInfoById();
    }

    /**
     * 接口名称：用户资料 - 保存
     * 接口路径：PUT/users
     */
    @PutMapping
    public ResponseEntity<Object> updateUserInfo(@RequestBody UserInfo userInfo){
        log.info("接口名称：用户资料 - 保存");
        return userService.updateUserInfo(userInfo);
    }


    /**
     * 接口名称：互相喜欢，喜欢，粉丝 - 统计
     * 接口路径：GET/users/counts
     */
    @GetMapping("counts")
    public ResponseEntity<Object> queryCounts(){
        log.info("接口名称：互相喜欢，喜欢，粉丝 - 统计");
        return userService.queryCounts();
    }

    /**
     * 接口名称：互相喜欢、喜欢、粉丝、谁看过我 - 翻页列表
     * 接口路径：GET/users/friends/:type
     */
    @GetMapping("friends/{type}")
    public ResponseEntity<Object> queryUserLikeList(@PathVariable Integer type,
                                                    @RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer pagesize){

        return userService.queryUserLikeList(type, page, pagesize);
    }

    /**
     * 接口名称：粉丝 - 喜欢
     * 接口路径：POST/users/fans/:uid
     */
    @PostMapping("/fans/{uid}")
    public ResponseEntity<Object> fansLike(@PathVariable Long uid){
        return userService.fansLike(uid);
    }


    /*
    * 接口名称：喜欢 - 取消
    * 接口路径：DELETE/users/like/:uid
    */
    @DeleteMapping("/like/{uid}")
    public ResponseEntity<Object> removeLike(@PathVariable Long uid){
        return todayBestService.removeLike(uid);
    }


    /*
    * 接口名称：是否喜欢（新增接口）
    * 接口路径：GET/users/:uid/alreadyLove
    */
    @GetMapping("/{uid}/alreadyLove")
    public ResponseEntity<Object> isLove(@PathVariable long uid){
        return ResponseEntity.ok(true);
    }
}
