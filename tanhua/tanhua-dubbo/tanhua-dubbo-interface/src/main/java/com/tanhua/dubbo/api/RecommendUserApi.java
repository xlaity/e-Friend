package com.tanhua.dubbo.api;


import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;

import java.util.ArrayList;
import java.util.List;

public interface RecommendUserApi {

    /**
     * 查询今日佳人
     * @param userId
     * @return
     */
    RecommendUser queryWithMaxScore(Long userId);

    /**
     * 分页查询推荐朋友
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    PageResult queryRecommendation(Long userId, Integer page, Integer pagesize);

    /**
     * 查询当前用户和某用户的缘分值
     * @param userId
     * @param recommentUserId
     * @return
     */
    long queryScore(Long userId, Long recommentUserId);

    /**
     * @param userId
     * @return
     */
    List<RecommendUser> queryRecommendUser(Long userId);

    /**
     * @param userId
     * @param likeUserId
     */
    void delete(Long userId, Long likeUserId);
}
