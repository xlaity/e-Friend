package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.Announcement;
import com.tanhua.dubbo.api.AnnouncementApi;
import com.tanhua.dubbo.mapper.AnnouncementMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AnnouncementApiImpl implements AnnouncementApi {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public IPage<Announcement> findByPage(Integer page, Integer pagesize) {
        IPage<Announcement> iPage = new Page<>(page, pagesize);
        return announcementMapper.selectPage(iPage, new QueryWrapper<>());
    }
}