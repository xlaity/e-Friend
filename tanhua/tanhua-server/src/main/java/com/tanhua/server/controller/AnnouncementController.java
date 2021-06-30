package com.tanhua.server.controller;

import com.tanhua.server.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 接口名称：公告列表
     * 接口路径：GET/messages/announcements
     */
    @GetMapping("/announcements")
    public ResponseEntity<Object> announcements(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer pagesize){
        return announcementService.findByPage(page, pagesize);
    }
}
