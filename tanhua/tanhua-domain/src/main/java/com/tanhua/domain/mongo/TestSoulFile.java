package com.tanhua.domain.mongo;

import com.tanhua.domain.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 作者：czd
 * 使用mongodb存储问卷
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "testSoul_file")
public class TestSoulFile implements Serializable {
    private static final long serialVersionUID = 5874126532504390567L;
    private ObjectId id;  //问卷主键id
    private String name;  //问卷名称
    private String cover;  //问卷封面
    private String level;  //问卷等级：初级，中级，高级
    private Integer star;  //问卷星级：2，3，5
    private List<Question> questions;  //问卷问题
    private Long created;  //创建时间
}
