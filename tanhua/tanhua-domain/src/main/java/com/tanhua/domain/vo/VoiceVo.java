package com.tanhua.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: tanhua-group9
 * @create: 2021-07-03 09:28
 **/
/**
 * 江杰
 * @Params:
 * @Return
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceVo implements Serializable {
        private Long id;//用户id
    private String avatar;//头像
    private String nickname;//用户昵称
    private String gender;//用户性别
    private Integer age;//用户的年龄
    private String soundUrl; //语音地址
    private Integer remainingTimes;  //剩余次数
}
