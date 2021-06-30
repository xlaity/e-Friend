package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.UserInfo;

/**
 * 用户详情服务接口
 */
public interface UserInfoApi {

    /**
     * 保存用户详情
     */
    void save(UserInfo userInfo);

    /**
     * 更新用户详情
     * @param userInfo
     */
    void update(UserInfo userInfo);

    /**
     * 根据id查询用户详情
     * @param id
     * @return
     */
    UserInfo findById(Long id);

    /**
     * 分页查询用户列表
     * @param page
     * @param pageSize
     * @return
     */
    public Page<UserInfo> findByPage(Integer page, Integer pageSize);
}
