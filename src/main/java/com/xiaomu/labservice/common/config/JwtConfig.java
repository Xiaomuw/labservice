package com.xiaomu.labservice.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * JWT 密钥
     */
    private String secret;

    /**
     * Access Token 过期时间（毫秒）
     */
    private Long accessTokenExpiration;

    /**
     * Refresh Token 过期时间（毫秒）
     */
    private Long refreshTokenExpiration;

    /**
     * 签发者
     */
    private String issuer;
}

