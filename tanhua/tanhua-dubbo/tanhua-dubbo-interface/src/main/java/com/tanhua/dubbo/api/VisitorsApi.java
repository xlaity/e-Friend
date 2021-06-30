package com.tanhua.dubbo.api;


import com.tanhua.domain.mongo.Visitors;

import java.util.List;

public interface VisitorsApi {
    /**
     * 第一次访问，显示前5个用户
     * @param userId
     * @param count
     * @return
     */
    List<Visitors> queryVisitors(Long userId, int count);

    /**
     * 不是第一次访问
     * @param userId
     * @param time
     * @return
     */
    List<Visitors> queryVisitors(Long userId, Long time);
}
