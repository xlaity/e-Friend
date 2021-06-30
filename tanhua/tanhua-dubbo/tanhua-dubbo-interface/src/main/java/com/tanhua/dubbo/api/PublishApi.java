package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.PageResult;

import java.util.List;

/**
 * 圈子服务接口
 */
public interface PublishApi {

    /**
     * 发布动态
     * @param publish
     */
    void save(Publish publish);

    /**
     * 查询好友动态
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    PageResult findByTimeLine(Integer page, Integer pagesize, Long userId);

    /**
     * 查询推荐动态
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    PageResult queryRecommendPublishList(Integer page, Integer pagesize, Long userId);

    /**
     * 查询某用户的动态
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    PageResult queryMyPublishList(Integer page, Integer pagesize, Long userId);

    /**
     * 根据id查询动态
     * @param id
     * @return
     */
    Publish findById(String id);

    /**
     * 修改动态状态
     * @param publishId
     * @param state
     */
    void updateState(String publishId, Integer state);

    /**
     * 根据pid集合批量查询动态
     * @param pidList
     * @return
     */
    List<Publish> findByPids(List<Long> pidList);
}
