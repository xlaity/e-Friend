package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.BlackList;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.mapper.BlackListMapper;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 黑名单服务接口实现类
 */
@Service
public class BlackListApiImpl implements BlackListApi {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private BlackListMapper blackListMapper;


    @Override
    public IPage<UserInfo> findBlackList(Integer page, Integer pagesize, Long userId) {
        IPage<UserInfo> ipage = new Page<>(page, pagesize);
        return userInfoMapper.findBlackList(ipage, userId);
    }

    @Override
    public void deleteBlackUser(Long userId, Long uid) {
        QueryWrapper<BlackList> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("black_user_id", uid);
        blackListMapper.delete(wrapper);
    }
}
