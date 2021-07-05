package com.tanhua.server.service;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.UserSum;
import com.tanhua.domain.vo.VoiceVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.UserSumApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.bson.types.ObjectId;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.domain.mongo.Voice;
import com.tanhua.dubbo.api.VoiceApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @program: tanhua-group9
 * @create: 2021-07-02 18:37
 **/
@Service
@Slf4j
public class VoiceService {
    //注入api
    @Reference
    private VoiceApi voiceApi;
    @Autowired
    private OssTemplate ossTemplate;
    // 注入fastdfs文件上传客户端对象
    // 注入web服务器对象, 主要用来获取上传地址
    @Autowired
    private FdfsWebServer fdfsWebServer;
    @Autowired
    private FastFileStorageClient storageClient;
    @Reference
    private UserInfoApi userInfoApi;
    @Reference
    private UserSumApi userSumApi;
    @Autowired
    private RedisTemplate<String,String>redisTemplate;

    /**
     * 江杰
     *
     * @Params:
     * @Return
     */
    //上传语音功能
    public ResponseEntity<Object> save(MultipartFile soundFile) throws Exception {
        if (soundFile == null) {
            return ResponseEntity.status(500).body("数据发送异常");
        } else {
            //将语音文件上传到fastDFS
            String originalFilename = soundFile.getOriginalFilename();
            // 获取文件扩展名
            String substring = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            StorePath storePath = storageClient.uploadFile(soundFile.getInputStream(), soundFile.getSize(), substring, null);
            //获取上传文件的地址
            String url = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();
            //封装数据保存到MongoDB
            Voice voice = new Voice();
            voice.setId(ObjectId.get());
            voice.setUserId(UserHolder.getUserId());
            voice.setVoiceUrl(url);
            voice.setVoId(0);
            voice.setCreated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            //封装数据记住此时的 void先不设置
            voiceApi.save(voice);
            return ResponseEntity.ok(null);
        }
    }

    /**
     * 江杰
     *
     * @Params:
     * @Return
     */
    //获取语音信息
    public ResponseEntity<Object> getVoice() {
        //首先查询所有//不能是自己发的语音
        List<Integer> voIds = voiceApi.findAll(UserHolder.getUserId());
        if (voIds == null || voIds.size() == 0) {
            return ResponseEntity.status(500).body("没有发现桃花语音");
        } else {
            //随机数获取void
            Random ra = new Random();
            int n = ra.nextInt(voIds.size());
            //获取了这个语音的编号id
            Integer integer = voIds.get(n);
            //获取用户消息和语音消息
            Voice voice = voiceApi.findOne(integer, UserHolder.getUserId());
            if (voice == null) {
                return ResponseEntity.status(500).body("没有相关语音信息");
            } else {
                VoiceVo voiceVo = new VoiceVo();
                //查询用户信息
                Long userId = voice.getUserId();
                UserInfo userInfo = userInfoApi.findById(userId);
                if (userInfo == null) {
                    return ResponseEntity.status(500).body("当前用户不存在");
                }
                //封装之前需要查询用户能够访问的次数
                UserSum userSum = userSumApi.findOne(UserHolder.getUserId());
                //如果数据为空就造一条数据到数据库中
                if (userSum == null) {
                    UserSum userSum1 = new UserSum();
                    userSum1.setId(new ObjectId());
                    userSum1.setUserId(UserHolder.getUserId());
                    userSum1.setUserNum(10);
                    userSumApi.save(userSum1);
                }
                if (userSumApi.findOne(UserHolder.getUserId()).getUserNum() == 0) {
                    return ResponseEntity.status(403).body("今日上限，不能获取");
                } else {
                    //封装数据
                    BeanUtils.copyProperties(userInfo, voiceVo);
                    voiceVo.setId(userInfo.getId());
                    voiceVo.setSoundUrl(voice.getVoiceUrl());
                    voiceVo.setRemainingTimes(userSumApi.findOne(UserHolder.getUserId()).getUserNum() - 1);
                    String str = "user_info_"+userInfo.getId();
                    //将这个对象存入redis
                    redisTemplate.opsForValue().set(str,"1");
                    //被获取后就需要将这条语音进行删除
                    voiceApi.delete(integer);
                    log.info("成功删除：");
                    //将userSum表格进行修改次数
                    userSumApi.update(UserHolder.getUserId());
                    return ResponseEntity.ok(voiceVo);
                }
            }
        }
    }
}

