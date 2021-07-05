package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.UserSum;

/**
 * @program: tanhua-group9
 * @create: 2021-07-03 12:16
 **/
public interface UserSumApi {
    UserSum findOne(Long id);
    /**
     * 江杰
     * @Params:
     * @Return
     */
    //修改用户次数
    void update(Long id);
    /**
     * 江杰
     * @Params:
     * @Return
     */
    //更新次数api
    void updateTimes();

    void save(UserSum userSum1);
}
