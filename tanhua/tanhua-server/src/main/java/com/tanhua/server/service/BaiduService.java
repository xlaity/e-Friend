package com.tanhua.server.service;

import com.tanhua.dubbo.api.UserLocationApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BaiduService {

    @Reference
    private UserLocationApi userLocationApi;

    /**
     * 接口名称：上报地理信息
     */
    public ResponseEntity<Object> saveLocation(Double longitude, Double latitude, String addrStr) {
        // 上报地理位置，参数1-经度，参数2-维度，参数3-地址描述，参数4-当前用户id
        userLocationApi.saveLocation(longitude, latitude, addrStr, UserHolder.getUserId());

        return ResponseEntity.ok(null);
    }
}
