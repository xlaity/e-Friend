package com.tanhua.dubbo.api;

import com.tanhua.domain.db.User;

import java.util.List;

/**
 * 暴露的服务接口
 */
public interface UserApi {

    /**
     * 根据手机号查询用户
     * @param mobile
     * @return
     */
    User findByMobile(String mobile);

    /**
     * 保存用户
     * @param user
     * @return
     */
    Long save(User user);

    /**
     * 修改用户
     * @param updateUser
     */
    void update(User updateUser);

    /**
     * 查询所有用户
     * @return
     */
    List<User> findAll();
}
