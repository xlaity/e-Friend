package com.tanhua.domain.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ops extends BasePojo{
    private Integer id;
    private Integer userId;
    private Integer freezingTime;
    private Integer freezingRange;
    private String reasonsForFreezing;
    private String frozenRemarks;
}
