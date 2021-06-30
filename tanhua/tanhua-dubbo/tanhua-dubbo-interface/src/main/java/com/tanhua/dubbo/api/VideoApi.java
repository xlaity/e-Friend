package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;

import java.util.List;

/**
 * 小视频服务接口
 */
public interface VideoApi {

    /**
     * 分页查询小视频列表
     * @param page
     * @param pagesize
     * @return
     */
    PageResult findByPage(Integer page, Integer pagesize);

    /**
     * 保存小视频
     * @param video
     */
    void save(Video video);

    /**
     * 视频用户关注
     * @param followUser
     */
    void followUser(FollowUser followUser);

    /**
     * 视频用户关注-取消
     * @param userId
     * @param uid
     */
    void unfollowUser(Long userId, Long uid);

    /**
     * 分页查询某用户发布的小视频列表
     * @param page
     * @param pagesize
     * @param uid
     * @return
     */
    PageResult findVideosList(Integer page, Integer pagesize, Long uid);

    /**
     * 根据id查询小视频
     * @param videoId
     * @return
     */
    Video findById(String videoId);

    /**
     * 根据vid集合批量查询小视频
     * @param vidList
     * @return
     */
    List<Video> findByVids(List<Long> vidList);
}
