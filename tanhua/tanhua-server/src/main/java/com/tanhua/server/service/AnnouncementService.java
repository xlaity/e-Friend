package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.domain.db.Announcement;
import com.tanhua.domain.vo.AnnouncementVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.AnnouncementApi;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnnouncementService {

    @Reference
    private AnnouncementApi announcementApi;

    /**
     * 接口名称：公告列表
     */
    public ResponseEntity<Object> findByPage(Integer page, Integer pagesize) {
        // 1.调用API查询分页数据
        IPage<Announcement> iPage = announcementApi.findByPage(page, pagesize);

        // 获取当前页的数据
        List<Announcement> announcementList = iPage.getRecords();

        // 2.封装vo集合
        List<AnnouncementVo> result = new ArrayList<>();
        if (announcementList != null) {
            // 将公告信息转成vo
            for (Announcement announcement : announcementList) {
                AnnouncementVo vo = new AnnouncementVo();
                BeanUtils.copyProperties(announcement, vo);
                vo.setId(announcement.getId().toString());
                // 日期格式化
                vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(announcement.getCreated()));
                result.add(vo);
            }
        }
        // 3.构造返回的分页对象
        PageResult pageResult = new PageResult(page, pagesize, (int) iPage.getTotal(), result);
        return ResponseEntity.ok(pageResult);
    }
}