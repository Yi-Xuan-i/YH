package com.yixuan.yh.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;

import java.util.Date;
import java.util.Map;

@Lazy
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtUtils {

    private String signKey;
    private Long expire;

    public String generateJwt(Map<String, Object> claims) {
        return generateJwt(signKey, expire, claims);
    }

    public String generateJwt(String signKey, Map<String, Object> claims) {
        return generateJwt(signKey, expire, claims);
    }

    public String generateJwt(long expire, Map<String, Object> claims) {
        return generateJwt(signKey, expire, claims);
    }

    /**
     * 生成JWT
     */
    public String generateJwt(String signKey, long expire, Map<String, Object> claims) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, signKey) // 签名算法
                .setClaims(claims) // 自定义内容(载荷)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
    }

    /**
     * 解析JWT
     */
    public Claims parseJwt(String jwt) {
        return Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

    /**
     * 判断JWT是否过期（如果JWT是错误也会视为过期）
     */
    public boolean isExpired(String jwt) {
        try {
            parseJwt(jwt);
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}