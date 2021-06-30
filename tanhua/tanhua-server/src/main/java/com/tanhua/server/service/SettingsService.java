package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * 通用设置业务层
 */
@Service
public class SettingsService {

    @Reference
    private SettingsApi settingsApi;

    @Reference
    private QuestionApi questionApi;

    @Reference
    private BlackListApi blackListApi;

    /**
     * 接口名称：用户通用设置 - 读取
     */
    public ResponseEntity<Object> querySettings() {
        // 1.获取当前登录用户信息
        User user = UserHolder.get();

        // 2.调用服务提供者api查询当前用户的通知设置
        Settings settings = settingsApi.findByUserId(user.getId());

        // 3.调用服务提供者api查询当前用户的陌生人问题
        Question question = questionApi.findByUserId(user.getId());

        // 4.封装返回结果
        SettingsVo vo = new SettingsVo();
        // 4.1 封装通知设置查询信息
        if (settings != null) {
            BeanUtils.copyProperties(settings, vo);
        }
        // 4.2 封装陌生人问题和手机号码
        if (question != null) {
            vo.setStrangerQuestion(question.getTxt());
        }
        vo.setPhone(user.getMobile());

        return ResponseEntity.ok(vo);
    }

    /**
     * 接口名称：通知设置 - 保存
     */
    public ResponseEntity<Object> saveNotification(Settings param) {
        // 1.获取当前登录用户信息
        Long userId = UserHolder.getUserId();

        // 2.根据登录用户id查询通知设置
        Settings settings = settingsApi.findByUserId(userId);

        // 3.判断，如果用户通知设置不存在，就新增，
        if (settings == null) {
            // 3.1 没有查到通知设置，执行保存
            settings = new Settings();
            // 将参数内容拷贝至settings对象中
            BeanUtils.copyProperties(param, settings);
            settings.setUserId(userId);
            settingsApi.save(settings);

        } else {
            // 3.2 查询到通知设置，执行修改
            settings.setLikeNotification(param.getLikeNotification());
            settings.setPinglunNotification(param.getPinglunNotification());
            settings.setGonggaoNotification(param.getGonggaoNotification());
            settingsApi.update(settings);
        }
        return ResponseEntity.ok(null);
    }

    /**
     * 接口名称：设置陌生人问题 - 保存
     */
    public ResponseEntity<Object> saveQuestion(String content) {
        // 1.获取当前登录用户信息
        Long userId = UserHolder.getUserId();

        // 2.根据登录用户id查询陌生人
        Question question = questionApi.findByUserId(userId);

        // 3.判断
        if (question == null) {
            // 3.1 如果陌生人问题不存在，就新增
            question = new Question();
            question.setTxt(content);
            question.setUserId(userId);
            questionApi.save(question);

        } else {
            // 3.2 如果陌生人问题存在，就更新
            question.setTxt(content);
            questionApi.update(question);
        }

        return ResponseEntity.ok(null);
    }

    /**
     * 接口名称：黑名单 - 翻页列表
     */
    public ResponseEntity<Object> blacklist(Integer page, Integer pagesize) {
        // 1.获取登录用户id
        Long userId = UserHolder.getUserId();

        // 2.调用服务提供者api分页查询
        IPage<UserInfo> ipage = blackListApi.findBlackList(page, pagesize, userId);

        // 3.封装返回结果
        PageResult pageResult = new PageResult(page, pagesize, (int) ipage.getTotal(), ipage.getRecords());

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 接口名称：黑名单 - 移除
     */
    public ResponseEntity<Object> deleteBlackUser(Long uid) {
        // 1.获取登录用户id
        Long userId = UserHolder.getUserId();

        // 2.调用服务提供者api删除数据
        blackListApi.deleteBlackUser(userId, uid);

        return ResponseEntity.ok(null);
    }
}
