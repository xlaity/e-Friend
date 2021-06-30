package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.domain.db.UserInfo;

/**
 * 黑名单服务接口
 */
public interface BlackListApi {

    /**
     * 分页查询用户的黑名单列表
     * @param page 当前页
     * @param pagesize 页大小
     * @param userId 用户id
     * @return
     */
    IPage<UserInfo> findBlackList(Integer page, Integer pagesize, Long userId);

    /**
     * 移除黑名单
     * @param userId
     * @param uid
     */
    void deleteBlackUser(Long userId, Long uid);
}
