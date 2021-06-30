package com.tanhua.dubbo.api;


import com.tanhua.domain.vo.UserLocationVo;

import java.util.List;

public interface UserLocationApi {

    /**
     * 上报地理位置
     * @param longitude
     * @param latitude
     * @param addrStr
     * @param userId
     */
    void saveLocation(Double longitude, Double latitude, String addrStr, Long userId);

    /**
     * 搜附近
     * @param userId
     * @param distance
     * @return
     */
    List<UserLocationVo> searchNear(Long userId, Long distance);
}
