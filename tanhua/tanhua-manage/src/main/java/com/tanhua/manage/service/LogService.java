package com.tanhua.manage.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tanhua.manage.mapper.LogMapper;
import com.tanhua.manage.domain.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
public class LogService extends ServiceImpl<LogMapper, Log> {
    
}