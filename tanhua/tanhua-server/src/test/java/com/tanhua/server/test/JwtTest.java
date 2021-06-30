package com.tanhua.server.test;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试jwt
 */
public class JwtTest {

    @Test
    public void testJwt(){
        // 1.定义加密内容
        Map<String, Object> map = new HashMap<>();
        map.put("id",  1);
        map.put("mobile", "18000110011");

        // 2.定义加密密钥
        String secret = "itcast";

        // 3.生成jwt字符串
        String token = Jwts.builder()
                .setClaims(map)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        System.out.println("生成token：" + token);

        // 4.解析token，获取map集合
        Map<String, Object> body = (Map<String, Object>)
                Jwts.parser().setSigningKey(secret).parse(token).getBody();

        System.out.println("body内容：" + body);
    }
}
