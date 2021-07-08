package com.tanhua.dubbo.api.impl;

import com.mongodb.client.result.DeleteResult;
import com.tanhua.domain.mongo.Voice;
import com.tanhua.domain.vo.VoiceVo;
import com.tanhua.dubbo.api.VoiceApi;
import com.tanhua.dubbo.utils.IdService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: tanhua-group9
 * @create: 2021-07-02 18:50
 **/
@Service(timeout = 500000)
public class VoiceApiImpl implements VoiceApi {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IdService idService;
    /**
     * 江杰
     * @Params:
     * @Return
     */
    @Override
    public void save(Voice voice) {
        //设置void
        Long voId = idService.getNextId("tanhua_voice");
        //获取自动设置的void自增
        voice.setVoId(voId.intValue());
        // 将数据保存到MongoDB
        //分析为什么不存入redis  由于这里的数据变化幅度比较大所有不传入redis
        mongoTemplate.save(voice, "tanhua_voice");
    }
    /**
     * 江杰
     * @Params:
     * @Return
     */
    //查询语音编号
    @Override
    public List<Integer> findAll(Long userId) {
        Query query = new Query(Criteria.where("userId").ne(userId));
        List<Voice> voices = mongoTemplate.find(query, Voice.class);
        if (voices == null || voices.size() == 0) {
            return null;
        } else {
            List<Integer> voIds = new ArrayList<>();
            for (Voice voice : voices) {
                voIds.add(voice.getVoId());
            }
            return voIds;
        }
    }
    /**
     * 江杰
     * @Params:
     * @Return
     */
    //获取单个用户的视频信息
    @Override
    public Voice findOne(Integer integer, Long userId) {
        Query query = new Query(Criteria.where("voId").is(integer).and("userId").ne(userId));
        Voice one = mongoTemplate.findOne(query, Voice.class);
        if (one == null) {
           return null;
        } else {
            return one;
        }
    }
    /**
     * 江杰
     * @Params:
     * @Return
     */
    //根据void删除语音
    @Override
    public void delete(Integer integer) {
        Query query = new Query(Criteria.where("voId").is(integer));
        mongoTemplate.remove(query, Voice.class);

    }

}
