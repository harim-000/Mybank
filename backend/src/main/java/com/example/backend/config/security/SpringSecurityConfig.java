package com.example.backend.config.security;

import com.example.backend.config.security.member.JwtAuthenticationFilter;
import com.example.backend.config.security.member.JwtAuthorizationFilter;
import com.example.backend.config.security.member.MemberDetailsService;
import com.example.backend.repository.MemberRepository;
import com.example.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.ApplicationContext;

/**
 * The type Spring security config.
 */
// Spring Security 설정파일
// WebSecurityConfigurerAdapter is deprecated
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SpringSecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final com.example.backend.config.security.CorsConfig corsConfig;

    /**
     * Password encoder password encoder.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager.
     *
     * @return the authentication manager
     * @throws Exception the exception
     */
    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Security filter chain security filter chain.
     *
     * @param http the http
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .addFilter(corsConfig.corsFilter());

        http.authorizeRequests()
                .antMatchers("/admin/**").access("hasRole('ADMIN')")
                .antMatchers("/login").permitAll() // 수정된 부분
                .antMatchers("/user/**").permitAll() // 수정된 부분
                .antMatchers("/community").anonymous()
                .antMatchers("/mypage").anonymous()
                .antMatchers("/api/**").permitAll()
                .anyRequest().permitAll()
                .and()
                .csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable();

        http.addFilter(new JwtAuthorizationFilter(authenticationManager(), memberRepository))
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), memberService));

        return http.build();
    }

    /**
     * Web security customizer web security customizer.
     *
     * @return the web security customizer
     */
// configure(WebSecurity web) is deprecated
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/js/**", "/fonts/**", "../static/**", "/favicon.ico");
    }

}