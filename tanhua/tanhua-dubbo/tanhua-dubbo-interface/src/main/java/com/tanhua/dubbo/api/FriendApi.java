package com.tanhua.dubbo.api;


import com.tanhua.domain.vo.PageResult;

public interface FriendApi {

    /**
     * 保存好友关系
     * @param userId
     * @param friendId
     */
    void save(Long userId, long friendId);

    /**
     * 分页查询好友列表
     * @param page
     * @param pagesize
     * @param keyword
     * @param userId
     * @return
     */
    PageResult queryContractList(Integer page, Integer pagesize, String keyword, Long userId);
}
