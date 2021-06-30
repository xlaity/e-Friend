package com.tanhua.manage.utils;

import cn.hutool.core.date.DateUtil;

import java.math.BigDecimal;
import java.util.Date;

public class ComputeUtil {
    
    /**
     * 计算环比
     *
     * @param current 本期计数
     * @param last 上一期计数
     * @return 环比
     */
    public static BigDecimal computeRate(Integer current, Integer last) {
        BigDecimal result;
        if (last == 0) {
            // 当上一期计数为零时，此时环比增长为倍数增长
            result = new BigDecimal((current - last) * 100);
        } else {
            // 2代表精度，保留两位小数
            // BigDecimal.ROUND_HALF_DOWN 代表舍入模式
            result = BigDecimal.valueOf((current - last) * 100)
                    .divide(BigDecimal.valueOf(last), 2, BigDecimal.ROUND_HALF_DOWN);
        }
        return result;
    }

    /**
     * 计算某个日期相隔offset的日期
     * 例如：
     * date = 2020-10-01, offset = 7
     * @return 2020-10-08
     */
    public static String offsetDay(Date date, int offset) {
        return DateUtil.offsetDay(date, offset).toDateStr();
    }
}