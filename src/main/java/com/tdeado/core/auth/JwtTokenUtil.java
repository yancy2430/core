package com.tdeado.core.auth;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT 工具类
 */
@Slf4j
public class JwtTokenUtil implements Serializable {

    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final Gson gson = new Gson();

    /**
     * 5天(毫秒)
     */
    private static final long EXPIRATION_TIME = 432000000;
    /**
     * JWT密码
     */
    private static final String SECRET = "secret";

    /**
     * 验证JWT
     */
    public static boolean validateToken(String token) {
        return (!isTokenExpired(token));

    }
    /**
     * 签发jwt 仅限系统间调用 用户操作流程中请勿使用此方法创建jwt
     */
    protected static String generateSystemToken() {
        Map<String, Object> claims = new HashMap<>(16);
        claims.put(CLAIM_KEY_USERNAME, "system");
        claims.put("iat",System.currentTimeMillis());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(Instant.now().toEpochMilli() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }
    /**
     * 获取token是否过期
     */
    public static boolean isTokenExpired(String token) {
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
     * 获取权限列表
     */
    public static List<String> getRoles(String token) {
        List<String> roles = (List<String>) getClaimsFromToken(token).get("roles");
        return roles;
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
//
//    public static void main(String[] args) {
//        User s = getUser("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiYXVkIjoidXNlciIsInJvbGVzIjpbInN5c3RlbSJdLCJleHAiOjE1Njk2NTI0MDcsImlhdCI6MTU2OTIyMDQwNzI0NSwidXNlciI6eyJtb2JpbGUiOiIxODE1MjczMzY2MCIsImlkIjoiMSIsImVtYWlsIjoiMTgxNTI3MzM2NjAiLCJ1c2VybmFtZSI6InVzZXIifX0.5o-rSkCSEPQLdUJkC55PoYEQm7ZLjDxrq0Vt8GGt_c0dHMsueNQYnQoVbYR3b5N74XSvoYAyOu-kMKj6sFPr0w");
//        System.err.println(s);
//    }


}
