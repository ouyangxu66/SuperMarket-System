package com.supermarket.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * 用于生成、解析和校验 JWT Token
 */
@Component
public class JwtUtils {

    // 令牌有效期 (单位: 毫秒) - 这里设为 24 小时
    private static final long EXPIRE = 1000 * 60 * 60 * 24;

    // 密钥 (Secret Key)
    // 注意：实际项目中应该配置在 application.yml 中，且长度至少要 32 个字符
    private static final String SECRET = "SupermarketProjectSecretKeyForGraduationDesign2024";

    // 生成安全的密钥对象
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成 Token
     * @param username 用户名
     * @return 加密后的 Token 字符串
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRE);

        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setSubject(username) // 将用户名放入 Token 主体
                .setIssuedAt(now)     // 签发时间
                .setExpiration(expiration) // 过期时间
                .signWith(key, SignatureAlgorithm.HS256) // 使用 HS256 算法和密钥签名
                .compact();
    }

    /**
     * 解析 Token 获取 Claims (载荷信息)
     * @param token Token 字符串
     * @return Claims 对象，包含 subject, expiration 等信息
     */
    public Claims getClaimsByToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // 解析失败（如：签名不对、Token过期、格式错误）
            System.err.println("JWT解析失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 校验 Token 是否有效
     * @param token Token 字符串
     * @return true=有效, false=无效
     */
    public boolean validateToken(String token) {
        Claims claims = getClaimsByToken(token);
        // 如果解析成功，且没有过期
        return claims != null && !isTokenExpired(claims);
    }

    /**
     * 判断 Token 是否过期
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    /**
     * 从 Token 中直接获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsByToken(token);
        return claims != null ? claims.getSubject() : null;
    }
}