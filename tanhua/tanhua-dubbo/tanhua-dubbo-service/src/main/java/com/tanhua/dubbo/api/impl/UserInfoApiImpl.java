package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 用户详情服务接口实现类
 */
@Service
public class UserInfoApiImpl implements UserInfoApi {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo findById(Long id) {
        return userInfoMapper.selectById(id);
    }

    public Page<UserInfo> findByPage(Integer page, Integer pageSize){
        Page<UserInfo> userInfoPage = userInfoMapper.selectPage(new Page<>(page, pageSize), null);
        return userInfoPage;
    }
}
