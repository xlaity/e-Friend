package com.tanhua.dubbo.api.impl;

import com.tanhua.domain.db.Ops;
import com.tanhua.dubbo.api.OpsApi;
import com.tanhua.dubbo.mapper.OpsMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class OpsApiImpl implements OpsApi {

    @Autowired
    private OpsMapper opsMapper;

    @Override
    public void accountFreeze(Ops ops) {
        opsMapper.insert(ops);
    }
}
