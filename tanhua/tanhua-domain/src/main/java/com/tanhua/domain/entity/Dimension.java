package com.tanhua.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 作者：czd
 * 报告描述实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dimension implements Serializable {

    private String key; //维度项（外向、判断、抽象、理性）
    private String value; //维度值（80%，70%，90%，60%）

}
