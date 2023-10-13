package com.example.backend.config.security.member;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.backend.domain.Member;
import com.example.backend.repository.MemberRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

/**
 * The type Jwt authorization filter.
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final MemberRepository memberRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);


    /**
     * Instantiates a new Jwt authorization filter.
     *
     * @param authenticationManager the authentication manager
     * @param memberRepository      the member repository
     */
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("토큰 검증");
        //logger.info(request.getRequestURI());

        // 헤더에 토큰을 담아 보냈는지(인증받은 사용자인지) 검사하는 과정
        String token = request.getHeader(JwtProperties.HEADER_STRING);

        // 헤더에 토큰이 없는 경우, JwtAuthorizationFilter을 통과
        if (token == null || !token.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            logger.info("토큰없음");
            logger.info(token);
            return;
        }

        // 헤더에 토큰이 있는 경우, 정보 확인
        if (!JwtTokenProvider.validateToken(token)) {
           // response.getUser_id().write("Token expired"); // 클라이언트에게 추가 정보 전달
            // 만료된 토큰 - TokenExpiredException 발생
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            logger.info("만료");
        } else {
            // 유효한 토큰 - 아이디를 통해 사용자 존재 여부/권한 확인
            String id = JwtTokenProvider.getId(token);
            logger.info("토큰 확인"+id);
            if (id != null) {

                Member member = memberRepository.selectOneById(id);

                // 권한 관리를 위해 SecurityContext에 인증 정보 저장
                MemberDetails memberDetails = new MemberDetails(member);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        memberDetails.getMember().getUser_id(),
                        memberDetails.getMember().getUser_pass(),
                        memberDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info(authentication.toString());

                // 토근 발급 시 한글인식 가능하게 설정하기(ASCII 설정)
                if(member.getUser_id()!=null) {
                    member.setUser_id(URLEncoder.encode(member.getUser_id(), StandardCharsets.UTF_8));
                }


                // 응답 헤더에 토큰과 유저 정보 추가
                response.setHeader(JwtProperties.HEADER_STRING, token);
                response.setHeader(JwtProperties.HEADER_USER, member.getUser_id());
            }
        }
        chain.doFilter(request, response);
    }
}
