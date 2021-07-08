package com.tanhua.dubbo.api;


import com.tanhua.domain.mongo.UserLike;
import com.tanhua.domain.vo.PageResult;

public interface UserLikeApi {
    /**
     * 互相喜欢数
     * @param userId
     * @return
     */
    Long queryEachLoveCount(Long userId);

    /**
     * 喜欢数
     * @param userId
     * @return
     */
    Long queryLoveCount(Long userId);

    /**
     * 粉丝数
     * @param userId
     * @return
     */
    Long queryFanCount(Long userId);

    /**
     * 查询喜欢用户列表
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    PageResult queryLoveList(Long userId, Integer page, Integer pagesize);

    /**
     * 查询互相喜欢列表
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    PageResult queryEachLoveList(Long userId, Integer page, Integer pagesize);

    /**
     * 查询粉丝列表
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    PageResult queryFanList(Long userId, Integer page, Integer pagesize);

    /**
     * 查询谁看过我列表
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    PageResult queryVisitorList(Long userId, Integer page, Integer pagesize);


    /**
     * 删除粉丝数据
     * @param userId
     * @param uid
     */
    void delete(Long userId, Long uid);

    /**
     * @param userLike
     */
    void addFans(UserLike userLike);

    /**
     * @param userId
     * @param uid
     */
    void deleteLike(Long userId, Long uid);

    /**
     * @param userId
     * @param likeUserId
     * @return
     */
    boolean isFans(Long userId, Long likeUserId);

}
