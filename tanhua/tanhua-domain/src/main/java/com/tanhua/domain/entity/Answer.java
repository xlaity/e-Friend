package com.tanhua.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 作者：czd
 * 接受问卷答案的实体类
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Answer implements Serializable {
    private String questionId; //问题编号
    private String optionId;  //答案编号
}
