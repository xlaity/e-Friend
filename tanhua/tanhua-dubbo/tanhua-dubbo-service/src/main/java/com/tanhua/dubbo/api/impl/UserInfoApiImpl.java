package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.entity.SimilarYou;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


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

    public Page<UserInfo> findByPage(Integer page, Integer pageSize) {
        Page<UserInfo> userInfoPage = userInfoMapper.selectPage(new Page<>(page, pageSize), null);
        return userInfoPage;
    }

    @Override
    public List<SimilarYou> findByIdList(List<Long> userIdList) {
        //查询userIdList的对应的用户数据
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.in("id", userIdList);
        List<UserInfo> userInfos = userInfoMapper.selectList(wrapper);

        //封装到SimilarYous中
        ArrayList<SimilarYou> similarYous = new ArrayList<>();
        if (userInfos != null && userInfos.size() >0){
            for (UserInfo userInfo : userInfos) {
                SimilarYou similarYou = new SimilarYou();
                similarYou.setId( Integer.valueOf(userInfo.getId().toString()));
                similarYou.setAvatar(userInfo.getAvatar());
                similarYous.add(similarYou);
            }
        }
        return similarYous;
    }
}
