package com.tanhua.dubbo.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 测试分页查询
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserInfoMapperTest {

    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 测试分页
     */
    @Test
    public void testPage(){
        // 1.创建分页对象，参数1-当前页，参数2-页大小
        IPage<UserInfo> page = new Page<>(1, 10);

        // 2.分页参数：查询条件
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper();
        queryWrapper.le("age", 100);

        IPage<UserInfo> result = userInfoMapper.selectPage(page, queryWrapper);

        List<UserInfo> userInfoList = result.getRecords(); // 当前页数据
        System.out.println("当前页数据 = " + userInfoList);
        System.out.println("总条数 = " + result.getTotal());
        System.out.println("总页数 = " + result.getPages());
        System.out.println("当前页 = " + result.getCurrent());
        System.out.println("页大小 = " + result.getSize());

    }

}
