package com.tanhua.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

/**
 * @program: tanhua-group9
 * @create: 2021-07-03 12:10
 * 这个表格用来记录用户获取桃花传音的次数
 **/
/**
 * 江杰
 * @Params:
 * @Return
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_sum")
public class UserSum implements java.io.Serializable{
    private ObjectId id ;
    private Long userId;
    private Integer userNum = 10;
}
