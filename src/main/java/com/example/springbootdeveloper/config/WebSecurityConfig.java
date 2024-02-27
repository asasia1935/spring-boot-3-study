package com.example.springbootdeveloper.config;

import com.example.springbootdeveloper.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailService userDetailService;

    // 스프링 시큐리티 기능 비활성화 (모든 곳에 인증, 인가 적용하지 않음) -> 일반적으로 정적 리소스에 설정함
    // + H2 콘솔의 데이터 확인에 대해서 비활성화
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/static/**");
    }

    // 특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests() // 인증, 인가 설정 -> 특정 경로에 대한 액세스 설정
                // 저 셋의 요청은 인증/인가 없이 접근 가능
                .requestMatchers("/login", "/signup", "/user").permitAll()
                // 이외의 URL에 대하여 설정 -> 인증만
                .anyRequest().authenticated()
                .and()
                // 폼 기반 로그인 설정 -> 로그인 페이지 경로 설정
                .formLogin()
                .loginPage("/login")
                // 로그인 완료되었을 때 이동할 경로
                .defaultSuccessUrl("/articles")
                .and()
                // 로그아웃 설정
                .logout()
                // 로그아웃이 완료되었을 때 이동할 경로 설정
                .logoutSuccessUrl("/login")
                // 로그아웃 이후에 세션을 전체 삭제할지 여부 설정
                .invalidateHttpSession(true)
                .and()
                // csrf 설정 비활성화 -> 공격 방지를 위해 설정하는 것이 좋음
                .csrf().disable()
                .build();
    }

    // 인증 관리자 관련 설정 -> 사용자 정보를 가져올 서비스 재정의 및 인증 방법 ex) LDAP, JDBC 기반 인증 설정할때 사용
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       BCryptPasswordEncoder bCryptPasswordEncoder,
                                                       UserDetailService userDetailService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                // 사용자 정보 서비스 설정
                // 사용자 정보를 가져올 서비스 설정 이때 UserDetailsService를 상속받은 클래스여야함
                .userDetailsService(userDetailService)
                // 비밀번호를 암호화 하기 위한 인코더 설정
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }

    // 패스워드 인코더를 빈으로 설정
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}