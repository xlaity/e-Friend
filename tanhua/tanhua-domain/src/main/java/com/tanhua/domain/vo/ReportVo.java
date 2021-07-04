package com.tanhua.domain.vo;

import com.tanhua.domain.entity.Dimension;
import com.tanhua.domain.entity.SimilarYou;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportVo implements Serializable {
    private String conclusion; //鉴定结果
    private String cover; //鉴定封面
    private List<Dimension> dimensions; //维度
    private List<SimilarYou> similarYou; //与你相似
}
