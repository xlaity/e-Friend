package com.tanhua.server.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    public static String createToken(Long id, String mobile, String secret) {
        // 定义要加密的数据
        Map<String,Object> map = new HashMap<>();
        map.put("id", id);
        map.put("mobile", mobile);

        // 生成token并返回
        return Jwts.builder()
                .setClaims(map) // 声明加密的数据
                .signWith(SignatureAlgorithm.HS256, secret) // 指定加密算法与密钥
                .compact();
    }
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
}