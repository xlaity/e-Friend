package com.tanhua.domain.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BasePojo{

    private Long id; // 主键
    private String mobile; //手机号
    private String password; //密码
//    private Date created; // 创建日期
//    private Date updated; // 修改日期
}