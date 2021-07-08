package com.tanhua.domain.vo;

import com.tanhua.domain.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestSoulListVo implements Serializable {
    private String id;  //问卷id
    private String name;  //问卷名称
    private String cover;  //问卷封面
    private String level;  //问卷等级：初级，中级，高级
    private Integer star;  //问卷星级：2，3，5
    private List<Question> questions;  //问卷问题
    private Integer isLock;  //是否锁住
    private String reportId; //报告id
}
