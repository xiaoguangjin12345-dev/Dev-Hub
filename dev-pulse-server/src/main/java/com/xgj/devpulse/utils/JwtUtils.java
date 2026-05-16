package com.xgj.devpulse.utils;

import com.xgj.devpulse.pojo.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component // 必须加这个，Spring 才会扫描它并注入配置
public class JwtUtils {

    // 静态变量，供全局调用
    private static String secret;
    private static String issuer;
    private static String audience;
    private static long expireMillis;
    private static Key KEY;

    // 使用 @Value 读取 yml 里的值
    @Value("${jwt.key}")
    private String yamlKey;

    @Value("${jwt.issuer}")
    private String yamlIssuer;

    @Value("${jwt.audience}")
    private String yamlAudience;

    @Value("${jwt.expireMinutes}")
    private long yamlExpireMinutes;

    // Spring 初始化后，把配置赋给静态变量
    @PostConstruct
    public void init() {
        secret = yamlKey;
        issuer = yamlIssuer;
        audience = yamlAudience;
        expireMillis = yamlExpireMinutes * 60 * 1000;
        KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 生成 Token
    public static String generateJwtToken(UserEntity user) {
        // 设置载荷值
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getUserID());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        claims.put("realName", user.getRealName() != null ? user.getRealName() : "");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer) // 调用静态变量
                .setAudience(audience)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireMillis))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 Token
    public static Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}