package com.tanhua.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageLikeVo {
    private String id;
    private String avatar;
    private String nickname;
    private String createDate;
}