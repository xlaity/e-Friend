package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.Voice;

import java.util.List;

/**
 * @program: tanhua-group9
 * @create: 2021-07-02 18:50
 **/
public interface VoiceApi {
    /**
     * 江杰
     * @Params:
     * @Return
     */
    void save(Voice voice);
    /**
     * 江杰
     * @Params:
     * @Return
     */
    //首先查询语音编号
    List<Integer> findAll(Long userId);
    /**
     * 江杰
     * @Params:
     * @Return
     */
    //获取单个用户上传的视频
    Voice findOne(Integer integer,Long userId);
    /**
     * 江杰
     * @Params:
     * @Return
     */
    //根据void删除语音
    void delete(Integer integer);

}
