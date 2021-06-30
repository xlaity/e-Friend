package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.User;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.mapper.UserMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 暴露的服务接口实现类
 */
@Service
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByMobile(String mobile) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", mobile);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public void update(User updateUser) {
        userMapper.updateById(updateUser);
    }

    @Override
    public List<User> findAll() {
        // 查询id小于等于80，因为环信用户如果超过100，需要充值
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("id", 80);
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList;
    }
}
