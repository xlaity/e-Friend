package com.tanhua.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * 江杰
 * @Params:
 * @Return
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tanhua_voice")
public class Voice implements java.io.Serializable {
    private ObjectId id;//主键id
    private Long userId;//上传的用户id
    private String voiceUrl;//上传文件的地址
    private String created; //上传时间
    private Integer voId ; //上传文件的编号
}
