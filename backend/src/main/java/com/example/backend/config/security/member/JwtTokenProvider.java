package com.example.backend.config.security.member;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;

/**
 * The type Jwt token provider.
 */
public class JwtTokenProvider {

    /**
     * Create token string.
     *
     * @param memberDetails the member details
     * @return the string
     */
    public static String createToken(MemberDetails memberDetails) {
        String token = JWT.create()
                .withSubject(memberDetails.getUsername())    // 이름
                .withClaim("exp", new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME)) // 만료 시간 클레임 추가
                .withClaim("id", memberDetails.getUsername())   // payload 부분에서 private 설정. private의 이름과 값 지정
                .sign(Algorithm.HMAC512(JwtProperties.SECRET)); // 시크릿키, 해싱 알고리즘

        return token;
    }

    /**
     * Gets token.
     *
     * @param request the request
     * @return the token
     */
// request에서 토큰 가져오기
    public static String getToken(HttpServletRequest request) {
        return request.getHeader(JwtProperties.HEADER_STRING);
    }

    /**
     * Validate token boolean.
     *
     * @param header the header
     * @return the boolean
     */
// 토큰 유효성 검증
    public static boolean validateToken(String header) {
        try {
            String token = header.replace(JwtProperties.TOKEN_PREFIX, "");
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token);
            Date expirationDate = decodedJWT.getExpiresAt();
            Date currentTime = new Date();
            return expirationDate.after(currentTime); // 만료 시간이 현재 시간보다 미래인 경우 true 반환
        } catch(TokenExpiredException e) {
            return false;
        }
    }

    /**
     * Gets id.
     *
     * @param header the header
     * @return the id
     */
// 토큰에서 아이디 정보 획득
    public static String getId(String header) {
        String token = header.replace(JwtProperties.TOKEN_PREFIX, "");
        return JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token).getClaim("id").asString();
    }
}
