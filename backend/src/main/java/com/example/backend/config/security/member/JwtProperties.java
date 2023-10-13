package com.example.backend.config.security.member;

/**
 * The type Jwt properties.
 */
public class JwtProperties {
    /**
     * The constant SECRET.
     */
    public static final String SECRET = "mybank"; // 서버만 알고 있는 개인키
    /**
     * The constant EXPIRATION_TIME.
     */
    public static final int EXPIRATION_TIME = 1000*60*60; // Jwt 토큰 만료시간
    /**
     * The constant TOKEN_PREFIX.
     */
    public static final String TOKEN_PREFIX = "Bearer ";   // Jwt 토큰 prefix로 반드시 붙여줘야 하는 문자열
    /**
     * The constant HEADER_STRING.
     */
    public static final String HEADER_STRING = "token";    // 헤더에 담을 토큰의 key값
    /**
     * The constant HEADER_USER.
     */
    public static final String HEADER_USER = "user";    // 헤더에 담을 유저의 key값
}
