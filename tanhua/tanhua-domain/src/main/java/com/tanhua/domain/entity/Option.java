package com.tanhua.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * 作者：czd
 * 问题选项实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Option implements Serializable {

    private String id;  //选项唯一id
    private String option;  //具体选项
}
