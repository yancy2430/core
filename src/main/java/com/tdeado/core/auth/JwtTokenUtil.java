package com.tdeado.core.auth;


import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

/**
 * JWT 工具类
 * @author yangze
 */
public class JwtTokenUtil implements Serializable {
    private static Gson gson = new Gson();
    private static final String CLAIM_KEY_USERNAME = "sub";

    /**
     * 5天(毫秒)
     */
    private static final long EXPIRATION_TIME = 432000000;
    /**
     * JWT密码
     */
    private static final String SECRET = "secret";


    /**
     * 签发JWT
     */
    public static String generateToken(Object user) {
        Map<String, Object> claims = new HashMap<>(16);
        claims.put("iat",System.currentTimeMillis());
        claims.put("user",gson.toJson(user));
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(Instant.now().toEpochMilli() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * 验证JWT
     */
    public static Boolean validateToken(String token) {
        return (!isTokenExpired(token));
    }

    /**
     * 获取token是否过期
     */
    public static Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 根据token获取username
     */
    public static String getUsernameFromToken(String token) {
        String username = getClaimsFromToken(token).getSubject();
        return username;
    }

    /**
     * 获取token的过期时间
     */
    public static Date getExpirationDateFromToken(String token) {
        Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration;
    }
    /**
     * 获取user
     */
    public static Object getUser(String token) {
        return getClaimsFromToken(token).get("user");
    }

    /**
     * 解析JWT
     */
    private static Claims getClaimsFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }


}