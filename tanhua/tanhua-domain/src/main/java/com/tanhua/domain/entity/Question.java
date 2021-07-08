package com.tanhua.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：czd
 * 问卷问题实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question implements Serializable {

    private String id; //问卷唯一id
    private String question ;//具体问题
    private List<Option> options; //选项

}
