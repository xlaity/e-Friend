package com.tanhua.dubbo.api.impl;
import com.tanhua.domain.vo.UserLocationVo;
import org.bson.types.ObjectId;

import com.tanhua.domain.mongo.UserLocation;
import com.tanhua.dubbo.api.UserLocationApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.w3c.dom.UserDataHandler;

import java.util.List;


@Service
public class UserLocationApiImpl implements UserLocationApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveLocation(Double longitude, Double latitude, String addrStr, Long userId) {
        // 判断用户地址位置信息是否存在
        Query query = new Query(Criteria.where("userId").is(userId));
        if(mongoTemplate.exists(query, UserLocation.class)){
            // 地理位置存在，更新数据
            Update update = new Update();
            // 设置当前位置
            update.set("location", new GeoJsonPoint(longitude, latitude));
            update.set("address", addrStr);
            update.set("updated", System.currentTimeMillis());
            update.set("lastUpdated", System.currentTimeMillis());
            mongoTemplate.updateFirst(query, update, UserLocation.class);

        }else{
            // 地理位置不存在，新增数据
            UserLocation userLocation = new UserLocation();
            userLocation.setUserId(userId);
            userLocation.setCreated(System.currentTimeMillis());
            userLocation.setLocation(new GeoJsonPoint(longitude, latitude));
            userLocation.setAddress(addrStr);
            userLocation.setUpdated(System.currentTimeMillis());
            userLocation.setLastUpdated(System.currentTimeMillis());
            mongoTemplate.save(userLocation);
        }
    }

    @Override
    public List<UserLocationVo> searchNear(Long userId, Long distance) {
        Query query = new Query(Criteria.where("userId").is(userId));

        // 查询当前用户的位置
        UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);
        GeoJsonPoint location = userLocation.getLocation();

        // 创建圆的半径
        Distance distanceObj = new Distance(distance/1000, Metrics.KILOMETERS);

        // 画圆，参数1-圆心，参数2-半径
        Circle circle = new Circle(location, distanceObj);

        // 查询地理位置在圆圈内的数据
        Query query2 = new Query(Criteria.where("location").withinSphere(circle));

        List<UserLocation> userLocationList = mongoTemplate.find(query2, UserLocation.class);

        return UserLocationVo.formatToList(userLocationList);
    }
}
