package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Settings;

/**
 * 通知设置服务接口
 */
public interface SettingsApi {

    /**
     * 查询用户的通知设置
     */
    Settings findByUserId(Long id);

    /**
     * 新增通知设置
     * @param settings
     */
    void save(Settings settings);

    /**
     * 更新通知设置
     * @param settings
     */
    void update(Settings settings);
}
