package com.tanhua.dubbo.api.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.Settings;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.dubbo.mapper.SettingsMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SettingsApiImpl implements SettingsApi {

    @Autowired
    private SettingsMapper settingsMapper;

    @Override
    public Settings findByUserId(Long id) {
        QueryWrapper<Settings> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", id);
        Settings settings = settingsMapper.selectOne(wrapper);
        return settings;
    }

    @Override
    public void save(Settings settings) {
        settingsMapper.insert(settings);
    }

    @Override
    public void update(Settings settings) {
        settingsMapper.updateById(settings);
    }
}
