package com.tanhua.dubbo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.domain.db.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 数据访问层
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 分页查询用户的黑名单列表
     */
    @Select("select info.* from tb_black_list b, tb_user_info info where user_id = #{userId} " +
            "and b.black_user_id = info.id")
    IPage<UserInfo> findBlackList(IPage<UserInfo> page, @Param("userId") Long userId);
}