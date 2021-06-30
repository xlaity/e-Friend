package com.tanhua.server.controller;

import com.tanhua.server.service.BaiduService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("baidu")
public class BaiduController {

    @Autowired
    private BaiduService baiduService;

    /**
     * 接口名称：上报地理信息
     * 接口路径：POST/baidu/location
     */
    @PostMapping("location")
    public ResponseEntity<Object> saveLocation(@RequestBody Map<String, Object> map) {
        Double longitude = (Double) map.get("longitude");
        Double latitude = (Double) map.get("latitude");
        String addrStr = (String) map.get("addrStr");
        return baiduService.saveLocation(longitude, latitude, addrStr);
    }
}
