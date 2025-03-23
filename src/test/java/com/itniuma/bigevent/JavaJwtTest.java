package com.itniuma.bigevent;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JavaJwtTest {
    // 生成jwt
    @Test
    void genToken() {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("username","ace");
        claims.put("password","123456");
        String token = JWT.create()
                .withClaim("user", claims) // 载荷中存放自定义信息
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365)) // 设置过期时间为365天
                .sign(Algorithm.HMAC256("itniuma")); // 设置加密算法
        System.out.println(token);
    }
    // 验证jwt
    @Test
    void parseToken() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJ1c2VyIjp7InBhc3N3b3JkIjoiMTIzNDU2IiwidXNlcm5hbWUiOiJhY2UifSwiZXhwIjoxNzQyNjMzMjIxfQ" +
                ".R5Q_G6v8stgpdDWNVpAmvPZc237tAckGOUc5Atrd7DM";
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256("itniuma")) // 设置加密算法
                .build()  // 创建验证对象
                .verify(token); // 验证token
        Map<String, Claim> claims = decodedJWT.getClaims(); // 获取载荷信息
        System.out.println(claims.get("user")); // 获取键为user的载荷
    }

}
