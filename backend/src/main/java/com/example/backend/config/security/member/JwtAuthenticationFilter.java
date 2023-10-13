package com.example.backend.config.security.member;

import com.example.backend.config.security.member.JwtProperties;
import com.example.backend.config.security.member.JwtTokenProvider;
import com.example.backend.config.security.member.MemberDetails;
import com.example.backend.domain.Member;
import com.example.backend.repository.MemberRepository;
import com.example.backend.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * The type Jwt authentication filter.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;
    private static final String Authenticated = "Authenticated";
    private static final String FAIL_Authenticated = "FAIL_Authenticated";


    // 인증 요청시 실행되는 함수 -> /login
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        logger.info("로그인 요청");
        // request에 있는 id,pw를 파싱해서 자바 Object로 받기
        ObjectMapper om = new ObjectMapper();
        Member member = null;
        try {
            member = om.readValue(request.getInputStream(), Member.class);
            logger.info(member);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // usernamepassword 토큰 생성 : 인증을 위해 id, pw 정보를 가지고 있는 토큰
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(member.getUser_id(), member.getUser_pass());
        logger.info(authenticationToken);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        MemberDetails memberDetails = (MemberDetails) authResult.getPrincipal();

        // 토큰 생성
        String jwtToken = JwtTokenProvider.createToken(memberDetails);
        // 응답 헤더에 토큰 추가
        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken);

        // 응답 헤더에 사용자 아이디 추가
        Member member = memberService.selectOneById(memberDetails.getUsername());
        System.out.println(member.getUser_id()+ "아이디 인코딩 시도");
        member.setUser_id(URLEncoder.encode(member.getUser_id(), StandardCharsets.UTF_8));
        // 응답 헤더에 사용자 아이디 추가
        response.setHeader(JwtProperties.HEADER_USER, member.getUser_id());
        // 응답 헤더에 토큰 만료 시간 추가
        Date expirationDate = new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME);
        response.addHeader("tokenExpiration", String.valueOf(expirationDate.getTime()));

        response.addHeader("checkToken", Authenticated);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
            logger.info("로그인 실패");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러 반환
        response.addHeader("checkToken", FAIL_Authenticated);

    }
}