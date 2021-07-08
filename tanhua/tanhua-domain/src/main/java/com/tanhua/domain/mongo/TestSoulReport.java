package com.tanhua.domain.mongo;

import com.tanhua.domain.db.BasePojo;
import com.tanhua.domain.entity.Dimension;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;


import java.io.Serializable;
import java.util.List;

/**
 * 作者：czd
 * 问卷详细报告
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "testSoul_report")
public class TestSoulReport implements Serializable {
    private static final long serialVersionUID = 5874126532504390567L;
    private ObjectId id;  //报告唯一id
    private String testSoulFileId;  //问卷id
    private Long userId; //用户id
    private Integer score;  //获取分数
    private Integer level;
    //等级 ：低于21分     1
    //      21-40分     2
    //      41分-55分:   3
    //      56以上：     4
    private String conclusion; //报告内容
    private String cover; //鉴定封面
    private List<Dimension> dimensions; //维度值
    private Long created; //创建时间
    private Long updated; //更新时间
}
