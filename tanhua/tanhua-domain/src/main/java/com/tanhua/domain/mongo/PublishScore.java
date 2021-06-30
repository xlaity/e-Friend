package com.tanhua.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("publish_score")
public class PublishScore {

    private ObjectId id;
    private Long userId;// 用户id
    private Long publishId; // 动态id
    private Double score; //得分
    private Long date; //时间戳
}