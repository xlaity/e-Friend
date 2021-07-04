package com.tanhua.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 作者：czd
 * 相似人群实体类
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SimilarYou implements Serializable {

    private Integer id; //用户编号
    private String avatar;//头像


}
