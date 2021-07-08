package com.tanhua.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 作者：czd
 * 单个用户的问卷答卷情况
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
//@Document(collection = "testSoul_file_user1")
public class TestSoulUser implements Serializable {
    private static final long serialVersionUID = 5874126532504390567L;
    private ObjectId id ;  //主键id
    private String testSoulId; //问卷id
    private String reportId; //报告id
    private Integer isLock;//是否被锁住，0解锁，1锁住
    private Long created;//创建时间

}
