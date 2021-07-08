package com.tanhua.server.controller;

import com.tanhua.server.service.VoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: tanhua-group9
 * @create: 2021-07-02 18:32
 **/
@RestController
@RequestMapping("/peachblossom")
public class VoiceController {
    @Autowired
    private VoiceService voiceService;
    /**
     * 江杰
     * @Params:
     * @Return
     */
    //上传语音功能
    @PostMapping
    public ResponseEntity<Object> peachBlossom(MultipartFile soundFile)throws Exception{
        return voiceService.save(soundFile);
    }
    /**
     *
     * @Params:江杰
     * @Return
     */
    //获取语音信息
    @GetMapping
    public ResponseEntity<Object> getVoice(){
        return voiceService.getVoice();
    }



}
