package com.tanhua.server.service;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.api.R;
import com.tanhua.commons.templates.HuanXinTemplate;
import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.mongo.UserLocation;
import com.tanhua.domain.vo.*;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.UserLocationApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TodayBestService {

    @Reference
    private UserInfoApi userInfoApi;

    @Reference
    private RecommendUserApi recommendUserApi;

    @Reference
    private QuestionApi questionApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Reference
    private UserLocationApi userLocationApi;

    /**
     * 接口名称：今日佳人
     */
    public ResponseEntity<Object> todayBest() {

        // 1.获取登录用户id
        Long userId = UserHolder.getUserId();

        // 2.调用服务提供者api查询今日佳人
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        if (recommendUser == null) {
            // 如果没有推荐数据，设置默认的数据
            recommendUser = new RecommendUser();
            recommendUser.setRecommendUserId(20l);
            recommendUser.setScore(80d);
        }

        // 3.封装返回结果
        TodayBestVo vo = new TodayBestVo();

        // 3.1 根据推荐用户id查询用户详情
        UserInfo userInfo = userInfoApi.findById(recommendUser.getRecommendUserId());

        // 3.2 封装userInfo到vo
        if (userInfo != null) {
            BeanUtils.copyProperties(userInfo, vo);
            // 封装tags标签
            if (userInfo.getTags() != null) {
                vo.setTags(userInfo.getTags().split(","));
            }
        }
        // 3.3 封装缘分值
        vo.setFateValue(recommendUser.getScore().longValue());

        return ResponseEntity.ok(vo);
    }

    /**
     * 接口名称：推荐朋友
     */
    public ResponseEntity<Object> queryRecommendation(RecommendQueryVo vo) {
        // 1.获取登录用户id
        Long userId = UserHolder.getUserId();

        // 2.调用服务提供者分页查询推荐朋友
        PageResult pageResult = recommendUserApi.queryRecommendation(userId, vo.getPage(), vo.getPagesize());

        // 获取当前页的数据
        List<RecommendUser> items = (List<RecommendUser>) pageResult.getItems();
        // 如果没有推荐数据，设置默认数据
        if (items == null) {
            items = new ArrayList<>();
            for (long i = 5; i < 10; i++) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setRecommendUserId(i);
                recommendUser.setScore(80d);
                items.add(recommendUser);
            }
        }

        // 3.封装返回结果，将items转换为voList
        List<TodayBestVo> voList = new ArrayList<>();
        for (RecommendUser recommendUser : items) {

            TodayBestVo todayBestVo = new TodayBestVo();

            // 3.1 根据推荐用户id查询用户详情
            UserInfo userInfo = userInfoApi.findById(recommendUser.getRecommendUserId());

            // 3.2 封装userInfo到vo
            if (userInfo != null) {
                BeanUtils.copyProperties(userInfo, todayBestVo);
                // 封装tags标签
                if (userInfo.getTags() != null) {
                    todayBestVo.setTags(userInfo.getTags().split(","));
                }
            }
            // 3.3 封装缘分值
            todayBestVo.setFateValue(recommendUser.getScore().longValue());

            // 3.4 添加vo对象到voList
            voList.add(todayBestVo);
        }

        // 4.重新设置pageResult的items值
        pageResult.setItems(voList);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 接口名称：佳人信息
     */
    public ResponseEntity<Object> queryPersonalInfo(Long recommendUserId) {
        // 1.调用服务提供者api查询用户详情
        UserInfo userInfo = userInfoApi.findById(recommendUserId);

        // 2.查询缘分值
        long score = recommendUserApi.queryScore(UserHolder.getUserId(), recommendUserId);

        // 3.封装返回结果
        TodayBestVo vo = new TodayBestVo();
        if (userInfo != null) {
            BeanUtils.copyProperties(userInfo, vo);
            if (userInfo.getTags() != null) {
                vo.setTags(userInfo.getTags().split(","));
            }
        }
        vo.setFateValue(score);

        return ResponseEntity.ok(vo);
    }

    /**
     * 接口名称：查询陌生人问题
     */
    public ResponseEntity<Object> strangerQuestions(Long userId) {
        Question question = questionApi.findByUserId(userId);
        String questionTxt = (question == null ? "你喜欢我吗？" : question.getTxt());
        return ResponseEntity.ok(questionTxt);
    }

    /**
     * 接口名称：回复陌生人问题
     */
    public ResponseEntity<Object> replyQuestions(Integer userId, String reply) {
        // 调用环信的api给推荐用户发送一条陌生人消息
        Map<String, Object> msg = new HashMap<>();
        msg.put("userId", UserHolder.getUserId()); // 发送者id

        // 查询发送者用户详情
        UserInfo userInfo = userInfoApi.findById(UserHolder.getUserId());
        msg.put("nickname", userInfo.getNickname());

        // 查询接收者的陌生人问题
        Question question = questionApi.findByUserId(userId.longValue());
        String questionTxt = (question == null ? "你喜欢我吗？" : question.getTxt());
        msg.put("strangerQuestion", questionTxt);

        // 回复的答案
        msg.put("reply", reply);

        // 参数1-接收的用户id，参数2-消息内容（json字符串）
        huanXinTemplate.sendMsg(userId.toString(), JSON.toJSONString(msg));

        return ResponseEntity.ok(null);
    }

    /**
     * 接口名称：搜附近
     */
    public ResponseEntity<Object> searchNear(String gender, Long distance) {

        // 1.查询服务提供者api获取附近的人
        List<UserLocationVo> userLocationList = userLocationApi.searchNear(UserHolder.getUserId(), distance);

        // 2.封装返回结果
        List<NearUserVo> voList = new ArrayList<>();

        if (userLocationList != null) {
            for (UserLocationVo userLocationVo : userLocationList) {
                // 查询用户详情
                UserInfo userInfo = userInfoApi.findById(userLocationVo.getUserId());

                // 排除自己
                if(UserHolder.getUserId().equals(userInfo.getId())){
                    continue;
                }
                // 排除性别不符合条件的
                if(!gender.equals(userInfo.getGender())){
                    continue;
                }

                // 创建vo
                NearUserVo vo = new NearUserVo();
                vo.setUserId(userInfo.getId());
                vo.setAvatar(userInfo.getAvatar());
                vo.setNickname(userInfo.getNickname());

                // 添加vo
                voList.add(vo);
            }
        }
        return ResponseEntity.ok(voList);
    }
}
