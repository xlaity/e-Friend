package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.domain.db.Announcement;

public interface AnnouncementApi {
    IPage<Announcement> findByPage(Integer page, Integer pagesize);
}